package com.example.selenaapp.ui.home

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.selenaapp.data.response.AnomalyTransactionsItem
import com.example.selenaapp.data.response.DataItem
import com.example.selenaapp.databinding.ActivityDetailAnomalyBinding
import java.text.NumberFormat
import java.util.Locale

class DetailAnomalyActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailAnomalyBinding

    companion object {
        const val EXTRA_TRANSACTION_ANOMALY_ID= "extra_transaction_anomaly_id"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailAnomalyBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val transaction = intent.getParcelableExtra<AnomalyTransactionsItem>(EXTRA_TRANSACTION_ANOMALY_ID)
        if (transaction != null) {
            val rupiahFormatter = NumberFormat.getCurrencyInstance(Locale("in", "ID"))
            val amount = rupiahFormatter.format(transaction.amount)
            binding.tvAmount.text = amount
            binding.tvTransactionIDValue.text = transaction.transactionId.toString()
            binding.tvDateValue.text = transaction.date.toString()
            binding.tvNotesValue.text = transaction.catatan.toString()
       }
    }
}