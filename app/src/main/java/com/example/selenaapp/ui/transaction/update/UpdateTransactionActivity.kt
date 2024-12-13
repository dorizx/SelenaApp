package com.example.selenaapp.ui.transaction.update

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.selenaapp.data.api.ApiConfig
import com.example.selenaapp.data.preference.UserPreference
import com.example.selenaapp.data.preference.dataStore
import com.example.selenaapp.data.response.DataItem
import com.example.selenaapp.databinding.ActivityUpdateTransactionBinding
import com.example.selenaapp.ui.main.MainActivity
import com.example.selenaapp.ui.transaction.detail.DetailTransactionActivity
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class UpdateTransactionActivity : AppCompatActivity() {

    private lateinit var binding: ActivityUpdateTransactionBinding
    private val calendar = Calendar.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUpdateTransactionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val transaction =
            intent.getParcelableExtra<DataItem>(DetailTransactionActivity.EXTRA_TRANSACTION_ID)

        if (transaction != null) {
            binding.amountEditText.setText(transaction.amount.toString())
            binding.dateEditText.setText(transaction.date)
            binding.notesEditText.setText(transaction.catatan)

            val transactionTypes = listOf("income", "expense")
            val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, transactionTypes)
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            binding.typeDropdown.adapter = adapter

            val currentTypeTransaction = transactionTypes.indexOf(transaction.transactionType)
            binding.typeDropdown.setSelection(currentTypeTransaction)
        }

        binding.dateEditText.setOnClickListener {
            showDatePicker()
        }

        binding.btnSend.setOnClickListener {
            val updatedAmount = binding.amountEditText.text.toString().toIntOrNull()
            val updatedType = binding.typeDropdown.selectedItem.toString()
            val updatedDate = binding.dateEditText.text.toString()
            val updatedNotes = binding.notesEditText.text.toString()

            // Validasi data
            if (updatedAmount == null || updatedDate.isEmpty() || updatedNotes.isEmpty()) {
                Toast.makeText(this, "Harap lengkapi semua data.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Lakukan update
            transaction?.transactionId?.let { id ->
                updateTransaction(id, updatedAmount, updatedType, updatedDate, updatedNotes)
            }
        }
    }

    private fun updateTransaction(
        transactionId: Int,
        amount: Int,
        type: String,
        date: String,
        notes: String
    ) {
        lifecycleScope.launch {
            val userPreference = UserPreference.getInstance(dataStore)
            val token = userPreference.getSession().first().token
            try {
                val response = ApiConfig.getApiService(token).updateTransaction(
                    transactionId = transactionId,
                    amount = amount,
                    transactionType = type,
                    date = date,
                    note = notes
                    )

                if (response.isSuccessful) {
                    Toast.makeText(this@UpdateTransactionActivity, "Transaksi berhasil diperbarui", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this@UpdateTransactionActivity, MainActivity::class.java)
                    intent.putExtra("navigate_to", "transactions")
                    startActivity(intent)
                    finish()
                } else {
                    Toast.makeText(this@UpdateTransactionActivity, "Gagal memperbarui transaksi", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(this@UpdateTransactionActivity, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
        }
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