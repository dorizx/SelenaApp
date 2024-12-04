package com.example.selenaapp.ui.transaction.file

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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File

class ShopeeFragment : Fragment() {

    private lateinit var selectFileButton: Button
    private lateinit var fileNameTextView: TextView
    private lateinit var uploadFileButton: Button
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
        val binding = inflater.inflate(R.layout.fragment_shopee, container, false)

        selectFileButton = binding.findViewById(R.id.selectFileButton)
        fileNameTextView = binding.findViewById(R.id.fileNameTextView)
        uploadFileButton = binding.findViewById(R.id.uploadButtonShopee)

        selectFileButton.setOnClickListener {
            selectFileLauncher.launch("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
        }

        uploadFileButton.setOnClickListener {
            uploadFile()
        }

        return binding
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

                    Log.d("ShopeeFragment", "Token: $token")
                    Log.d("ShopeeFragment", "User ID: $userId")

                    // Call API to upload the file
                    try {
                        val response = ApiConfig
                                .getApiService(token)
                                .addShopeeTransaction(userId, filePart)

                        CoroutineScope(Dispatchers.Main).launch {
                            if (response.isSuccessful) {
                                Toast.makeText(context, "Upload berhasil: ${response.body()?.message}", Toast.LENGTH_SHORT).show()
                            } else {
                                Toast.makeText(context, "Upload gagal: ${response.errorBody()?.string()}", Toast.LENGTH_SHORT).show()
                            }
                        }
                    } catch (e: Exception) {
                        CoroutineScope(Dispatchers.Main).launch {
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