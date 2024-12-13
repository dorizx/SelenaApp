package com.example.selenaapp.ui.transaction.file

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.example.selenaapp.data.api.ApiConfig
import com.example.selenaapp.data.preference.UserPreference
import com.example.selenaapp.data.preference.dataStore
import com.example.selenaapp.databinding.FragmentTokopediaBinding
import com.example.selenaapp.ui.main.MainActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File

class TokopediaFragment : Fragment() {

    private lateinit var fileNameTextView: TextView
    private var selectedFileUri: Uri? = null
    private var _binding: FragmentTokopediaBinding? = null
    private val binding get() = _binding!!

    private val selectFileLauncher = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            selectedFileUri = uri
            val fileName = getFileName(uri)
            if (fileName.isNotEmpty()) {
                fileNameTextView.text = "Nama File: $fileName"
            }
        } else {
            Log.d("File Picker", "No file selected")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTokopediaBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        fileNameTextView =
            binding.fileNameTextView

        binding.selectFileButton.setOnClickListener {
            selectFileLauncher.launch("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
        }

        binding.uploadButtonTokopedia.setOnClickListener {
            uploadFile()
        }
    }

    private fun getFileName(uri: Uri): String {
        var fileName = "Unknown"
        context?.let { context ->
            val cursor = context.contentResolver.query(uri, null, null, null, null)
            cursor?.let {
                if (it.moveToFirst()) {
                    val columnIndex = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                    if (columnIndex >= 0) {
                        fileName = it.getString(columnIndex)
                    }
                }
                it.close()
            }
        }

        if (!fileName.endsWith(".xlsx", ignoreCase = true)) {
            Toast.makeText(context, "File harus berupa Excel (.xlsx)", Toast.LENGTH_SHORT).show()
            return ""  // Return empty if file is not .xlsx
        }

        return fileName
    }

    private var uploadJob: Job? = null

    private fun uploadFile() {
        val context = context ?: return

        if (selectedFileUri == null) {
            Toast.makeText(context, "Pilih file terlebih dahulu", Toast.LENGTH_SHORT).show()
            return
        }

        val fileUri = selectedFileUri ?: return
        val fileName = getFileName(fileUri)

        if (fileName.isEmpty()) {
            // File not valid (e.g., not .xlsx)
            return
        }

        val file = File(context.cacheDir, fileName)
        val inputStream = context.contentResolver.openInputStream(fileUri)

        if (inputStream != null) {
            file.outputStream().use { output ->
                inputStream.copyTo(output)
            }

            val fileRequestBody =
                file.asRequestBody("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet".toMediaTypeOrNull())
            val filePart =
                MultipartBody.Part.createFormData("file-excel", file.name, fileRequestBody)

            uploadJob?.cancel()

            uploadJob = CoroutineScope(Dispatchers.IO).launch {
                val userPreference = UserPreference.getInstance(context.dataStore)
                userPreference.getSession().collect { userModel ->
                    val userId = userModel.userId
                    val token = userModel.token

                    try {
                        val response =
                            ApiConfig.getApiService(token).addTokopediaTransaction(userId, filePart)

                        if (isAdded) {
                            CoroutineScope(Dispatchers.Main).launch {
                                if (response.isSuccessful) {
                                    Toast.makeText(
                                        context,
                                        "Upload berhasil: ${response.body()?.message}",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    showSuccessDialog(context)
                                } else {
                                    Toast.makeText(
                                        context,
                                        "Upload gagal: ${response.errorBody()?.string()}",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }
                        }
                    } catch (e: Exception) {
                        if (isAdded) {
                            CoroutineScope(Dispatchers.Main).launch {
                                Toast.makeText(
                                    context,
                                    "Terjadi kesalahan: ${e.message}",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    }
                }
            }
        } else {
            Toast.makeText(context, "Gagal membaca file", Toast.LENGTH_SHORT).show()
        }
    }

    private fun showSuccessDialog(context: Context) {
        if (isAdded) {
            AlertDialog.Builder(requireContext()).apply {
                setTitle("Yeah!")
                setMessage("Anda berhasil upload. Ayo cek transaksi Anda.")
                setPositiveButton("Lanjut") { _, _ ->
                    val intent = Intent(context, MainActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                }
                create()
                show()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        uploadJob?.cancel()
        _binding = null
    }
}
