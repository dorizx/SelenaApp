package com.example.selenaapp.ui.login

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.selenaapp.data.preference.UserModel
import com.example.selenaapp.data.repository.UserRepository
import kotlinx.coroutines.launch

class LoginViewModel (private val repository: UserRepository) : ViewModel() {

    fun saveSession(user: UserModel) {
        viewModelScope.launch {

            repository.saveSession(user)
            Log.d("UserRepository", "Saving session with token: ${user.token}")
        }
    }

    fun login(email: String, password: String, callback: (Boolean, String?, String?, String?) -> Unit) {
        viewModelScope.launch {
            try {
                val response = repository.login(email, password)
                val token = response.token
                val userId = response.user?.id.toString()
                val name = response.user?.name.toString()
                if (!token.isNullOrEmpty()) {
                    Log.d("LoginViewModel", "Login successful: Token=$token")
                    callback(true, token, userId, name)
                } else {
                    Log.e("LoginViewModel", "Login failed: Token is null or empty")
                    callback(false, null, null, null)
                }
            } catch (e: Exception) {
                Log.e("LoginViewModel", "Login error: ${e.message}", e)
                callback(false, null, null, null)
            }
        }
    }
}