package com.example.selenaapp.ui.signup

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.selenaapp.data.repository.UserRepository
import com.example.selenaapp.data.response.SignupResponse
import kotlinx.coroutines.launch
import retrofit2.Response

class SignupViewModel (private val repository: UserRepository) : ViewModel() {

    fun signup(name: String, email: String, password: String, callback: (Response<SignupResponse>) -> Unit) {
        viewModelScope.launch {
            try {
                val response = repository.signup(name, email, password)
                callback(response)
            } catch (e: Exception) {
               e.printStackTrace()
               callback(Response.error(500, null))
            }
        }
    }
}