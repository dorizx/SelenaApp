package com.example.selenaapp.ui.transaction

import android.os.Bundle
import android.widget.ArrayAdapter
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.selenaapp.R
import com.example.selenaapp.databinding.ActivityFormAddTransactionBinding

class FormAddTransactionActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFormAddTransactionBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFormAddTransactionBinding.inflate(layoutInflater)
        setContentView(R.layout.activity_form_add_transaction)

        val transactionTypes = arrayOf("Pendapatan", "Penjualan")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, transactionTypes)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.typeDropdown.adapter = adapter
    }
}