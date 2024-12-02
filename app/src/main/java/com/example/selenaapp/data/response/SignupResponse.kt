package com.example.selenaapp.data.response

import com.google.gson.annotations.SerializedName

data class SignupResponse(

	@field:SerializedName("message")
	val message: String? = null,

	@field:SerializedName("user")
	val user: SignupUser? = null
)

data class SignupUser(

	@field:SerializedName("name")
	val name: String? = null,

	@field:SerializedName("id")
	val id: Int? = null,

	@field:SerializedName("email")
	val email: String? = null
)
