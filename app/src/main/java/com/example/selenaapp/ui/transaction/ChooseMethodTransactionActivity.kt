package com.example.selenaapp.ui.transaction

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.selenaapp.R
import com.example.selenaapp.databinding.ActivityChooseMethodTransactionBinding

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

    }
}