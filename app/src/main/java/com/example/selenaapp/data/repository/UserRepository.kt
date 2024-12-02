package com.example.selenaapp.data.repository

import com.example.selenaapp.data.api.ApiService
import com.example.selenaapp.data.response.LoginResponse
import com.example.selenaapp.data.response.SignupResponse

class UserRepository (private val api: ApiService) {

        suspend fun login(email: String, password: String) : LoginResponse {
            return api.login(email, password)
        }

        suspend fun signup(name: String, email: String, password: String) : SignupResponse {
          return api.signup(name, email, password)
        }

        suspend fun otp(email: String) = api.getOtp(email)
}