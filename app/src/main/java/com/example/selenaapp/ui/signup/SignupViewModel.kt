package com.example.selenaapp.ui.signup

import android.util.Patterns
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.selenaapp.data.repository.UserRepository
import com.example.selenaapp.data.response.SignupResponse
import kotlinx.coroutines.launch
import retrofit2.Response

class SignupViewModel (private val repository: UserRepository) : ViewModel() {

    private val _errorMessage = MutableLiveData<String?>()

    private val _emailError = MutableLiveData<String?>()
    val emailError: LiveData<String?> = _emailError

    private val _passwordError = MutableLiveData<String?>()
    val passwordError: LiveData<String?> = _passwordError

    private val _isFormValid = MutableLiveData<Boolean>()
    val isFormValid: LiveData<Boolean> = _isFormValid

    fun signup(name: String, email: String, password: String, callback: (Response<SignupResponse>) -> Unit) {
        viewModelScope.launch {
            try {
                val response = repository.signup(name, email, password)
                if (response.isSuccessful) {
                    callback(response)
                } else {
                    val errorMessage = response.errorBody()?.string() ?: "Pendaftaran gagal"
                    callback(Response.error(response.code(), response.errorBody()))
                    _errorMessage.value = errorMessage
                }
                callback(response)
            } catch (e: Exception) {
               e.printStackTrace()
               callback(Response.error(500, null))
            }
        }
    }
    fun validateEmail(email: String) {
        _emailError.value = when {
            email.isEmpty() -> "Email tidak boleh kosong"
            !Patterns.EMAIL_ADDRESS.matcher(email).matches() -> "Format email tidak valid"
            else -> null
        }
        checkFormValidity()
    }

    // Validate password
    fun validatePassword(password: String) {
        _passwordError.value = when {
            password.isEmpty() || password.length < 8 -> "Password tidak boleh kurang dari 8 karakter"
            else -> null
        }
        checkFormValidity()
    }

    // Check form validity
    private fun checkFormValidity() {
        _isFormValid.value = _emailError.value == null && _passwordError.value == null
    }

}