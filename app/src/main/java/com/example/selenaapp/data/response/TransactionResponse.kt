package com.example.selenaapp.data.response

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

data class TransactionResponse(

	@field:SerializedName("data")
	val data: List<DataItem?>? = null,

	@field:SerializedName("message")
	val message: String? = null
)

@Parcelize
data class DataItem(

	@field:SerializedName("transaction_id")
	val transactionId: Int? = null,

	@field:SerializedName("date")
	val date: String? = null,

	@field:SerializedName("amount")
	val amount: Int? = null,

	@field:SerializedName("user_id")
	val userId: Int? = null,

	@field:SerializedName("catatan")
	val catatan: String? = null,

	@field:SerializedName("transaction_type")
	val transactionType: String? = null
) : Parcelable
