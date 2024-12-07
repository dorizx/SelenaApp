package com.example.selenaapp

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.selenaapp.data.injection.Injection
import com.example.selenaapp.data.preference.UserPreference
import com.example.selenaapp.data.repository.UserRepository
import com.example.selenaapp.ui.login.LoginViewModel
import com.example.selenaapp.ui.main.MainViewModel
import com.example.selenaapp.ui.otp.OtpViewModel
import com.example.selenaapp.ui.signup.SignupViewModel
import com.example.selenaapp.ui.transaction.TransactionViewModel

class ViewModelFactory (private val repository: UserRepository,
                        private val userPreference: UserPreference)
    : ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(SignupViewModel::class.java) -> {
                return SignupViewModel(repository) as T
            }
            modelClass.isAssignableFrom(OtpViewModel::class.java) -> {
                return OtpViewModel(repository) as T
            }
            modelClass.isAssignableFrom(MainViewModel::class.java) -> {
                return MainViewModel(repository) as T
            }
            modelClass.isAssignableFrom(LoginViewModel::class.java) -> {
                return LoginViewModel(repository) as T
            }
            else -> throw IllegalArgumentException("Unknown ViewModel class")
        }
    }

    companion object {
        @Volatile
        private var INSTANCE: ViewModelFactory? = null
        @JvmStatic
        fun getInstance(context: Context): ViewModelFactory {
            if (INSTANCE == null) {
                synchronized(ViewModelFactory::class.java) {
                    INSTANCE = ViewModelFactory(
                        Injection.provideUserRepository(context),
                        Injection.provideUserPreference(context)
                    )
                }
            }
            return INSTANCE as ViewModelFactory
        }
    }
}