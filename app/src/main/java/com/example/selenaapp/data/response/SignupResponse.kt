package com.example.selenaapp.data.response

import com.google.gson.annotations.SerializedName

data class SignupResponse(

	@field:SerializedName("message")
	val message: String,

	@field:SerializedName("user")
	val user: SignupUser
)

data class SignupUser(

	@field:SerializedName("id")
	val id: Int,
	@field:SerializedName("name")
	val name: String,
	@field:SerializedName("email")
	val email: String?
)
