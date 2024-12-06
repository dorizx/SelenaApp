package com.example.selenaapp.data.response

import com.google.gson.annotations.SerializedName

data class UpdateResponse(

	@field:SerializedName("message")
	val message: String? = null,

	@field:SerializedName("transaction")
	val transaction: Transaction? = null
)

data class Transaction(

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
)
