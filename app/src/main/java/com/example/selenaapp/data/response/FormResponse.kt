package com.example.selenaapp.data.response

import com.google.gson.annotations.SerializedName

data class FormResponse(

	@field:SerializedName("transaction_id")
	val transactionId: String? = null,

	@field:SerializedName("message")
	val message: String? = null
)
