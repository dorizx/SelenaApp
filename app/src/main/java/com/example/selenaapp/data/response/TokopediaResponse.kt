package com.example.selenaapp.data.response

import com.google.gson.annotations.SerializedName

data class TokopediaResponse(

	@field:SerializedName("totalTransaksi")
	val totalTransaksi: Int? = null,

	@field:SerializedName("message")
	val message: String? = null
)