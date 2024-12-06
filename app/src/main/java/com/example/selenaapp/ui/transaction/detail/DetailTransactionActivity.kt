package com.example.selenaapp.ui.transaction.detail

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.selenaapp.data.api.ApiConfig
import com.example.selenaapp.data.preference.UserPreference
import com.example.selenaapp.data.preference.dataStore
import com.example.selenaapp.data.response.DataItem
import com.example.selenaapp.databinding.ActivityDetailTransactionBinding
import com.example.selenaapp.ui.main.MainActivity
import com.example.selenaapp.ui.transaction.TransactionFragment
import com.example.selenaapp.ui.transaction.update.UpdateTransactionActivity
import kotlinx.coroutines.launch

class DetailTransactionActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailTransactionBinding

    companion object {
        const val EXTRA_TRANSACTION_ID = "extra_transaction_id"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailTransactionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val transaction = intent.getParcelableExtra<DataItem>(EXTRA_TRANSACTION_ID)
        if (transaction != null) {
            binding.tvTransactionIDValue.text = transaction.transactionId.toString()
            binding.tvDateValue.text = transaction.date.toString()
            binding.tvStatusValue.text = transaction.transactionType.toString()
            binding.tvNotesValue.text = transaction.catatan.toString()
    }

        binding.btnDelete.setOnClickListener{
            deleteTransaction(transaction?.transactionId!!.toInt())
        }

        binding.btnUpdate.setOnClickListener{
            val intent = Intent(this, UpdateTransactionActivity::class.java)
            intent.putExtra(EXTRA_TRANSACTION_ID, transaction)
            startActivity(intent)
        }

    }


    private fun deleteTransaction(transactionId: Int) {
        lifecycleScope.launch {
            val userPreference = UserPreference.getInstance(dataStore)
            userPreference.getSession().collect { userModel ->
                val token = userModel.token
                if (!token.isNullOrEmpty()) {
                    try {
                        val response = ApiConfig.getApiService(token)
                            .deleteTransaction(transactionId)
                        if (response.isSuccessful) {
                            Toast.makeText(
                                this@DetailTransactionActivity,
                                "Transaksi berhasil dihapus",
                                Toast.LENGTH_SHORT
                            ).show()
                            navigateBackToTransactions()
                        } else {
                            Toast.makeText(
                                this@DetailTransactionActivity,
                                "Gagal menghapus transaksi",
                                Toast.LENGTH_SHORT
                            ).show()
                            navigateBackToTransactions()
                        }
                    } catch (e: Exception) {
                        Toast.makeText(
                            this@DetailTransactionActivity,
                            "Error: ${e.message}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } else {
                    Toast.makeText(
                        this@DetailTransactionActivity,
                        "Token tidak valid. Silakan login kembali.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    private fun navigateBackToTransactions() {
        finish()
    }


}