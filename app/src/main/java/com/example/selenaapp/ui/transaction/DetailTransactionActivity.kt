package com.example.selenaapp.ui.transaction

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.selenaapp.R
import com.example.selenaapp.data.response.DataItem
import com.example.selenaapp.databinding.ActivityDetailTransactionBinding

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

    }
}