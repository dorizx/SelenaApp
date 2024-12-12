package com.example.selenaapp.data.response

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

data class DashboardResponse(

	@field:SerializedName("anomalyTransactions")
	val anomalyTransactions: List<AnomalyTransactionsItem?>? = null,

	@field:SerializedName("financialAdvice")
	val financialAdvice: String? = null,

	@field:SerializedName("message")
	val message: String? = null,

	@field:SerializedName("transactionStats")
	val transactionStats: TransactionStats? = null
)

@Parcelize
data class AnomalyTransactionsItem(

	@field:SerializedName("date")
	val date: String? = null,

	@field:SerializedName("amount")
	val amount: Int? = null,

	@field:SerializedName("catatan")
	val catatan: String? = null,

	@field:SerializedName("transactionId")
	val transactionId: Int? = null
) : Parcelable

data class TransactionStats(

	@field:SerializedName("totalIncome")
	val totalIncome: Int? = null,

	@field:SerializedName("incomeTransationsSize")
	val incomeTransationsSize: Int? = null,

	@field:SerializedName("expenseTransationsSize")
	val expenseTransationsSize: Int? = null,

	@field:SerializedName("totalExpense")
	val totalExpense: Int? = null
)
