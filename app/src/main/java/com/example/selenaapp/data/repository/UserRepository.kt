package com.example.selenaapp.data.repository

import android.content.ContentValues.TAG
import android.util.Log
import com.example.selenaapp.data.api.ApiConfig
import com.example.selenaapp.data.preference.UserModel
import com.example.selenaapp.data.preference.UserPreference
import com.example.selenaapp.data.response.LoginResponse
import com.example.selenaapp.data.response.OtpResponse
import com.example.selenaapp.data.response.SignupResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import retrofit2.Response

class UserRepository private constructor(
    private val userPreference: UserPreference
) {

    suspend fun login(email: String, password: String): LoginResponse {
        return withContext(Dispatchers.IO) {
            val token = userPreference.getSession().map { it.token }.firstOrNull() ?: ""
            Log.d("UserRepository", "Making login request with token: $token")
            val response = ApiConfig.getApiService(token).login(email, password)
            Log.d("UserRepository", "Login response: $response")
            response
        }
    }

    suspend fun signup(name: String, email: String, password: String): Response<SignupResponse> {
        return withContext(Dispatchers.IO) {
            try {
                val token = userPreference.getSession().map { it.token }.firstOrNull() ?: ""
                ApiConfig.getApiService().signup(name, email, password)
            } catch (e: Exception) {
                Log.e(TAG, "Signup failed: ${e.message}", e)
                throw Exception("Network request failed: ${e.message}")
            }
        }
    }

    suspend fun verifyOtp(
        otp: String,
        name: String,
        email: String,
        password: String
    ): Response<OtpResponse> {
        return withContext(Dispatchers.IO) {
            try {
                val token = userPreference.getSession().map { it.token }.firstOrNull() ?: ""
                ApiConfig.getApiService().verifyOtp(otp, name, email, password)
            } catch (e: Exception) {
                throw Exception("OTP verification failed: ${e.message}")
            }
        }
    }

    //melihat session sudah login atau belum
    fun getSession(): Flow<UserModel> {
        return userPreference.getSession().map { user ->
            Log.d(TAG, "getSession: User data from preferences: $user")
            user
        }
    }

    suspend fun saveSession(user: UserModel) {
        userPreference.saveSession(user)
    }


    companion object {
        @Volatile
        private var instance: UserRepository? = null
        fun getInstance(
            userPreference: UserPreference
        ): UserRepository =
            instance ?: synchronized(this) {
                instance ?: UserRepository(userPreference)
            }.also { instance = it }
    }
}