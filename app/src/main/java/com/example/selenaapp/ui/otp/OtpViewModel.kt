package com.example.selenaapp.ui.otp

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.selenaapp.data.repository.UserRepository
import com.example.selenaapp.data.response.OtpResponse
import kotlinx.coroutines.launch

class OtpViewModel (private val repository: UserRepository) : ViewModel() {

    fun verifyOtp(
        otp: String,
        name: String,
        email: String,
        password: String,
        callback: (OtpResponse) -> Unit) {
        viewModelScope.launch {
            try {
                val response = repository.verifyOtp(otp, name, email, password)
                if (response.isSuccessful) {
                    val otpResponse = response.body()  // Ambil body dari response
                    if (otpResponse != null) {
                        callback(otpResponse)  // Panggil callback dengan data OtpResponse
                    } else {
                        callback(OtpResponse("Response body is null", null))
                    }
                } else {
                    callback(OtpResponse("OTP verification failed", null))
                }
            } catch (e: Exception) {
                e.printStackTrace()
                callback(OtpResponse("Gagal terhubung ke server", null))
            }
        }
    }

}
