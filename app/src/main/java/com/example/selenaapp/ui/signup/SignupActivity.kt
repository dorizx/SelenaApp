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
                    Toast.makeText(this, "Pendaftaran gagal: ${response.errorBody()}", Toast.LENGTH_SHORT).show()
                }
            }
        }
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