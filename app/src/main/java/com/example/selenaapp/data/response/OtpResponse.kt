package com.example.selenaapp.data.response

import com.google.gson.annotations.SerializedName

data class OtpResponse(

	@field:SerializedName("password")
	val password: String? = null,

	@field:SerializedName("name")
	val name: String? = null,

	@field:SerializedName("otp")
	val otp: String? = null,

	@field:SerializedName("email")
	val email: String? = null
)
