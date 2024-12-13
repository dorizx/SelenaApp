package com.example.selenaapp.ui.login

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.selenaapp.ViewModelFactory
import com.example.selenaapp.data.preference.UserModel
import com.example.selenaapp.data.response.LoginResponse
import com.example.selenaapp.ui.main.MainActivity
import com.example.selenaapp.databinding.ActivityLoginBinding
import com.example.selenaapp.ui.signup.SignupActivity

class LoginActivity : AppCompatActivity() {
    private val viewModel by viewModels<LoginViewModel> {
        ViewModelFactory.getInstance(this)
    }
    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        showLoading(false)

        setupAction()

        binding.signupButton.setOnClickListener {
            startActivity(Intent(this, SignupActivity::class.java))
        }

        supportActionBar?.hide()

    }

    private fun setupAction() {
        binding.loginButton.setOnClickListener {
            showLoading(true)
            val email = binding.emailEditText.text.toString()
            val password = binding.passwordEditText.text.toString()
            val passwordLayout = binding.passwordEditTextLayout

            if (email.isEmpty()) {
                binding.emailEditTextLayout.error = "Email tidak boleh kosong"
                showLoading(false)
                return@setOnClickListener
            } else {
                binding.emailEditTextLayout.error = null
            }

            if (password.isEmpty() || password.length < 8) {
                passwordLayout.error = "Password harus minimal 8 karakter"
                showLoading(false)
                return@setOnClickListener
            } else {
                passwordLayout.error = null
            }

            viewModel.login(email, password) { success, token, userId, name ->
                if (success && !token.isNullOrEmpty() && !userId.isNullOrEmpty()) {
                    showLoading(false)
                    // Simpan sesi dengan token
                    val userID = userId.toInt()
                    val nameUser = name.toString()
                    Log.d("LoginActivity", "Login successful: name=$nameUser")
                    viewModel.saveSession(UserModel(nameUser, email, token, true, userID, false))
                    Log.d("LoginActivity", "Login successful: Token saved=$token")
                    AlertDialog.Builder(this).apply {
                        setTitle("Yeah!")
                        setMessage("Anda berhasil login. Selamat datang di Selena App!")
                        setPositiveButton("Lanjut") { _, _ ->
                            val intent = Intent(context, MainActivity::class.java)
                            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                            startActivity(intent)
                            finish()
                        }
                        create()
                        show()
                    }
                } else {
                    showLoading(false)
                    AlertDialog.Builder(this).apply {
                        setTitle("Oops!")
                        setMessage("Login gagal. Silakan coba lagi")
                        setPositiveButton("OK") { dialog, _ ->
                            dialog.dismiss()
                        }
                        create()
                        show()
                    }
                }
            }
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressIndicator.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finishAffinity()
    }

}