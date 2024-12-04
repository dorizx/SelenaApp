package com.example.selenaapp.ui.transaction.form

import android.app.DatePickerDialog
import android.os.Bundle
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import com.example.selenaapp.databinding.ActivityFormAddTransactionBinding
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class FormAddTransactionActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFormAddTransactionBinding
    private val calendar = Calendar.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFormAddTransactionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val transactionTypes = arrayOf("Pendapatan", "Pengeluaran")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, transactionTypes)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.typeDropdown.adapter = adapter

        binding.dateEditText.setOnClickListener{
            showDatePicker()
        }
    }

    private fun showDatePicker() {
        val dateSetListener = DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
            calendar.set(Calendar.YEAR, year)
            calendar.set(Calendar.MONTH, month)
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)

            // Format date to string
            val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            binding.dateEditText.setText(dateFormat.format(calendar.time))
        }

        // Show DatePickerDialog with current date as default
        DatePickerDialog(
            this,
            dateSetListener,
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

}