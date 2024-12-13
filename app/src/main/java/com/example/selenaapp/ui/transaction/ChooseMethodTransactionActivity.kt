package com.example.selenaapp.ui.transaction

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.selenaapp.databinding.ActivityChooseMethodTransactionBinding
import com.example.selenaapp.ui.transaction.file.FileUploadActivity
import com.example.selenaapp.ui.transaction.form.FormAddTransactionActivity

class ChooseMethodTransactionActivity : AppCompatActivity() {
    private lateinit var binding: ActivityChooseMethodTransactionBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChooseMethodTransactionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnManual.setOnClickListener {
            val intent = Intent(this, FormAddTransactionActivity::class.java)
            startActivity(intent)
        }

        binding.btnFile.setOnClickListener {
            val intent = Intent(this, FileUploadActivity::class.java)
            startActivity(intent)
        }
    }
}