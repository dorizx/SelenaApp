package com.example.selenaapp.ui.transaction.file

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import com.example.selenaapp.R
import com.example.selenaapp.data.api.ApiConfig
import com.example.selenaapp.data.preference.UserPreference
import com.example.selenaapp.data.preference.dataStore
import com.example.selenaapp.ui.main.MainActivity
import com.example.selenaapp.ui.transaction.TransactionFragment
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File


class TokopediaFragment : Fragment() {

    private lateinit var selectFileButton: Button
    private lateinit var fileNameTextView: TextView
    private lateinit var uploadButton: Button
    private var selectedFileUri: Uri? = null

    private val selectFileLauncher = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            selectedFileUri = uri
            val fileName = getFileName(uri)
            fileNameTextView.text = "Nama File: $fileName"
        } else {
            Log.d("File Picker", "No file selected")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the fragment layout
        val binding = inflater.inflate(R.layout.fragment_tokopedia, container, false)

        // Initialize UI components
        selectFileButton = binding.findViewById(R.id.selectFileButton)
        fileNameTextView = binding.findViewById(R.id.fileNameTextView)
        uploadButton = binding.findViewById(R.id.uploadButtonTokopedia)

        // Set click listeners
        selectFileButton.setOnClickListener {
            selectFileLauncher.launch("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
        }

        uploadButton.setOnClickListener {
            uploadFile()
        }

        return binding
    }

    // onViewCreated - digunakan untuk setup dan konfigurasi tambahan setelah tampilan fragment dibuat
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Anda bisa menambahkan inisialisasi atau setup lainnya di sini
        Log.d("TokopediaFragment", "onViewCreated called")
    }

    // onDestroyView - membersihkan referensi tampilan dan sumber daya yang digunakan oleh fragment
    override fun onDestroyView() {
        super.onDestroyView()
        // Membersihkan referensi tampilan
        // Jika Anda memiliki komponen atau variabel yang perlu dibersihkan, lakukan di sini
        Log.d("TokopediaFragment", "onDestroyView called")
    }

    private fun getFileName(uri: Uri): String {
        var fileName = "Unknown"
        val cursor = context?.contentResolver?.query(uri, null, null, null, null)
        cursor?.let {
            if (it.moveToFirst()) {
                val columnIndex = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                if (columnIndex >= 0) {
                    fileName = it.getString(columnIndex)
                }
            }
            it.close()
        }

        if (!fileName.endsWith(".xlsx", ignoreCase = true)) {
            Toast.makeText(context, "File harus berupa Excel (.xlsx)", Toast.LENGTH_SHORT).show()
            return ""  // Return empty if file is not .xlsx
        }

        return fileName
    }

    private fun uploadFile() {
        val context = context ?: return

        if (selectedFileUri == null) {
            Toast.makeText(context, "Pilih file terlebih dahulu", Toast.LENGTH_SHORT).show()
            return
        }

        val fileUri = selectedFileUri ?: return
        val file = File(context.cacheDir, getFileName(fileUri))
        val inputStream = context.contentResolver.openInputStream(fileUri)

        if (inputStream != null) {
            file.outputStream().use { output ->
                inputStream.copyTo(output)
            }

            val fileRequestBody = file.asRequestBody("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet".toMediaTypeOrNull())
            val filePart = MultipartBody.Part.createFormData("file-excel", file.name, fileRequestBody)

            CoroutineScope(Dispatchers.IO).launch {
                // Get user_id from preferences
                val userPreference = UserPreference.getInstance(context.dataStore)
                userPreference.getSession().collect { userModel ->
                    val userId = userModel.userId
                    val token = userModel.token

                    // Call API to upload the file
                    try {
                        val response = ApiConfig
                            .getApiService(token)
                            .addTokopediaTransaction(userId, filePart)

                        // Pastikan untuk kembali ke Main thread setelah task selesai
                        withContext(Dispatchers.Main) {
                            // Cek apakah fragment masih terhubung ke activity
                            if (isAdded) {
                                if (response.isSuccessful) {
                                    Toast.makeText(context, "Upload berhasil: ${response.body()?.message}", Toast.LENGTH_SHORT).show()

                                    // Hentikan semua task lainnya di background
                                    // Tidak ada lagi coroutine yang perlu dijalankan
                                    val intent = Intent(context, MainActivity::class.java)
                                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                    startActivity(intent)

                                } else {
                                    Toast.makeText(context, "Upload gagal: ${response.errorBody()?.string()}", Toast.LENGTH_SHORT).show()
                                }
                            } else {
                                Log.w("TokopediaFragment", "Fragment not attached to activity, cannot start MainActivity.")
                            }
                        }
                    } catch (e: Exception) {
                        // Pastikan untuk menangani error dan kembali ke Main Thread
                        withContext(Dispatchers.Main) {
                            Toast.makeText(context, "Terjadi kesalahan: ${e.message}", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        } else {
            Toast.makeText(context, "Gagal membaca file", Toast.LENGTH_SHORT).show()
        }
    }
}
