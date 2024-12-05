package com.example.selenaapp.ui.transaction

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.selenaapp.R
import com.example.selenaapp.data.response.DataItem

class DetailTransactionActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_TRANSACTION_ID = "extra_transaction_id"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_transaction)

        val transaction = intent.getParcelableExtra<DataItem>(EXTRA_TRANSACTION_ID)
    }
}