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

class ShopeeFragment : Fragment() {

    private lateinit var selectFileButton: Button
    private lateinit var fileNameTextView: TextView
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
        val binding = inflater.inflate(R.layout.fragment_tokopedia, container, false)

        selectFileButton = binding.findViewById(R.id.selectFileButton)
        fileNameTextView = binding.findViewById(R.id.fileNameTextView)

        selectFileButton.setOnClickListener {
            selectFileLauncher.launch("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
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
        selectedFileUri?.let {
            // Logika upload file Excel ke server/API
            Toast.makeText(context, "File berhasil dipilih: ${getFileName(it)}", Toast.LENGTH_SHORT).show()
        } ?: run {
            Toast.makeText(context, "Pilih file terlebih dahulu", Toast.LENGTH_SHORT).show()
        }
    }
}