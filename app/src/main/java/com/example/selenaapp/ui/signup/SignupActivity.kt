package com.example.selenaapp.ui.signup

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import com.example.selenaapp.ViewModelFactory
import com.example.selenaapp.databinding.ActivitySignupBinding
import com.example.selenaapp.ui.otp.OtpActivity
import org.json.JSONObject

class SignupActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySignupBinding
    private val signupViewModel: SignupViewModel by viewModels {
        ViewModelFactory.getInstance(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        observeViewModel()

        binding.emailEditText.addTextChangedListener { text ->
            signupViewModel.validateEmail(text.toString())
        }

        binding.passwordEditText.addTextChangedListener { text ->
            signupViewModel.validatePassword(text.toString())
        }

        binding.signupButton.setOnClickListener {
            val name = binding.nameEditText.text.toString()
            val email = binding.emailEditText.text.toString()
            val password = binding.passwordEditText.text.toString()

            signupViewModel.signup(name, email, password) { response ->
                if (response.isSuccessful) {
                    val intent = Intent(this, OtpActivity::class.java).apply {
                        putExtra(OtpActivity.EXTRA_NAME, name)
                        putExtra(OtpActivity.EXTRA_EMAIL, email)
                        putExtra(OtpActivity.EXTRA_PASSWORD, password)
                    }
                    startActivity(intent)
                } else {
                    val errorMessage = try {
                        val errorBody = response.errorBody()?.string()
                        if (!errorBody.isNullOrEmpty()) {
                            val json = JSONObject(errorBody)
                            json.optString("message", "Terjadi kesalahan")
                        } else {
                            when (response.code()) {
                                409 -> "Email sudah terdaftar!"
                                400 -> "Nama, email, dan password perlu diisi!"
                                else -> "Terjadi kesalahan. Kode: ${response.code()}"
                            }
                        }
                    } catch (e: Exception) {
                        "Terjadi kesalahan: ${e.message}"
                    }
                    Toast.makeText(this, "Pendaftaran gagal: $errorMessage", Toast.LENGTH_LONG).show()
                }
            }
        }
        supportActionBar?.hide()
    }
    private fun observeViewModel() {
        // Observe email error
        signupViewModel.emailError.observe(this) { error ->
            binding.emailEditTextLayout.error = error
        }

        // Observe password error
        signupViewModel.passwordError.observe(this) { error ->
            binding.passwordEditTextLayout.error = error
        }

        // Observe form validity
        signupViewModel.isFormValid.observe(this) { isValid ->
            binding.signupButton.isEnabled = isValid
        }
    }

}