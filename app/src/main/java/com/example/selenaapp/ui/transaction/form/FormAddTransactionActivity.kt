package com.example.selenaapp.ui.transaction.form

import android.app.DatePickerDialog
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.selenaapp.data.preference.UserPreference
import com.example.selenaapp.data.preference.dataStore
import com.example.selenaapp.databinding.ActivityFormAddTransactionBinding
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class FormAddTransactionActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFormAddTransactionBinding
    private val calendar = Calendar.getInstance()

    private val viewModel: FormViewModel by lazy {
        FormViewModel(UserPreference.getInstance(dataStore))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFormAddTransactionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupSpinner()

        observeViewModel()
        setupAction()

        binding.dateEditText.setOnClickListener {
            showDatePicker()
        }
    }

    private fun setupSpinner() {
        val transactionTypes = arrayOf("income", "expense")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, transactionTypes)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.typeDropdown.adapter = adapter
    }

    private fun setupAction() {
        lifecycleScope.launch {
            viewModel.userSession.collect { user ->
                val userId = user.userId.toString()
                Log.d("FormAddTransaction", "User ID: $userId") // Log userId

                binding.btnSend.setOnClickListener {
                    val amount = binding.amountEditText.text.toString().toIntOrNull()
                    Log.d("FormAddTransaction", "Amount: $amount") // Log amount

                    val transactionType = binding.typeDropdown.selectedItem?.toString()
                    Log.d("FormAddTransaction", "Transaction Type: $transactionType") // Log transactionType

                    val date = binding.dateEditText.text.toString()
                    Log.d("FormAddTransaction", "Date: $date") // Log date

                    val note = binding.notesEditText.text.toString()
                    Log.d("FormAddTransaction", "Note: $note") // Log note

                    // Validasi input
                    if (amount != null && transactionType != null && date.isNotEmpty()) {
                        Log.d("FormAddTransaction", "All inputs are valid, sending data...")
                        viewModel.addTransaction(userId, amount, transactionType, date, note)
                    } else {
                        Log.d("FormAddTransaction", "Invalid input: amount=$amount, type=$transactionType, date=$date")
                        showToast("Harap lengkapi semua input!")
                    }
                }
            }
        }
    }

    private fun observeViewModel() {
        viewModel.uploadResult.observe(this) { result ->
            result.onSuccess { message ->
                showToast(message)
                finish()
            }.onFailure { error ->
                showToast(error.message ?: "Terjadi kesalahan")
            }
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
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