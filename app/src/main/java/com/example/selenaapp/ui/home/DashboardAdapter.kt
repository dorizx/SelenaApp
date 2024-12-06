package com.example.selenaapp.ui.home

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.selenaapp.R
import com.example.selenaapp.data.response.AnomalyTransactionsItem
import com.example.selenaapp.data.response.DataItem
import com.example.selenaapp.data.response.TransactionResponse
import com.example.selenaapp.ui.transaction.detail.DetailTransactionActivity
import java.text.NumberFormat
import java.util.Locale

class DashboardAdapter(private val transactionList: List<AnomalyTransactionsItem?>) :
    RecyclerView.Adapter<DashboardAdapter.TransactionViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransactionViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_transaction_history, parent, false)
        return TransactionViewHolder(view)
    }

    override fun onBindViewHolder(holder: TransactionViewHolder, position: Int) {
        val transaction = transactionList[position]
        val formattedAmount = NumberFormat.getNumberInstance(Locale("id", "ID"))
            .format(transaction?.amount)
        holder.dateTextView.text = transaction?.date
        holder.amountTextView.text = "Rp. $formattedAmount"

        holder.itemView.setOnClickListener {
            val intent = Intent(holder.itemView.context, DetailTransactionActivity::class.java)
            intent.putExtra(DetailTransactionActivity.EXTRA_TRANSACTION_ID, transaction)
            holder.itemView.context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int = transactionList.size

    class TransactionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val dateTextView: TextView = itemView.findViewById(R.id.tanggalLabel)
        val amountTextView: TextView = itemView.findViewById(R.id.totalUang)
    }

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<TransactionResponse>() {
            override fun areItemsTheSame(
                oldItem: TransactionResponse,
                newItem: TransactionResponse
            ): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(
                oldItem: TransactionResponse,
                newItem: TransactionResponse
            ): Boolean {
                return oldItem == newItem
            }
        }
    }
}