package com.example.selenaapp.data.response

import com.google.gson.annotations.SerializedName

data class DetailResponse(

	@field:SerializedName("data")
	val data: Data? = null,

	@field:SerializedName("message")
	val message: String? = null
)

data class Data(

	@field:SerializedName("transaction_id")
	val transactionId: Int? = null,

	@field:SerializedName("date")
	val date: String? = null,

	@field:SerializedName("amount")
	val amount: Int? = null,

	@field:SerializedName("updatedAt")
	val updatedAt: String? = null,

	@field:SerializedName("user_id")
	val userId: Int? = null,

	@field:SerializedName("catatan")
	val catatan: String? = null,

	@field:SerializedName("createdAt")
	val createdAt: String? = null,

	@field:SerializedName("status")
	val status: String? = null
)
