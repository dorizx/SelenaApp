package com.example.selenaapp.ui.settings

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.selenaapp.data.api.ApiConfig
import com.example.selenaapp.data.preference.UserPreference
import com.example.selenaapp.databinding.FragmentSettingsBinding
import com.example.selenaapp.ui.help.HelpActivity
import com.example.selenaapp.ui.login.LoginActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

// Ekstensi Context untuk DataStore
val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class SettingsFragment : Fragment() {

    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Inisialisasi ViewModel
        val pref = SettingsPreference.getInstance(requireContext().dataStore)
        val viewModelFactory = SettingsViewModelFactory(pref)
        val mainViewModel = ViewModelProvider(this, viewModelFactory)
            .get(SettingsViewModel::class.java)

        setupUserInfo()
        setupThemeSwitch(mainViewModel)
        setupListeners(mainViewModel)
    }

    private fun setupUserInfo() {
        viewLifecycleOwner.lifecycleScope.launch {
            val userPreference = UserPreference.getInstance(requireContext().dataStore)
            val userModel = userPreference.getSession().first()
            binding.tvUser.text = userModel.name
            binding.tvEmail.text = userModel.email
        }
    }

    private fun setupThemeSwitch(mainViewModel: SettingsViewModel) {
        // Mengatur mode gelap/terang berdasarkan pengaturan
        mainViewModel.getThemeSettings().observe(viewLifecycleOwner) { isDarkModeActive ->
            if (isDarkModeActive) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                binding.switch2.isChecked = true
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                binding.switch2.isChecked = false
            }
        }

        // Menangani perubahan pada switch untuk tema
        binding.switch2.setOnCheckedChangeListener { _, isChecked ->
            mainViewModel.saveThemeSetting(isChecked)
        }
    }

    private fun setupListeners(mainViewModel: SettingsViewModel) {
        // Tombol logout
        binding.btnLogout.setOnClickListener {
            viewLifecycleOwner.lifecycleScope.launch {
                val userPreference = UserPreference.getInstance(requireContext().dataStore)
                userPreference.logout()
                val intent = Intent(requireContext(), LoginActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
            }
        }

        // Tombol help
        binding.cardHelp.setOnClickListener {
            val intent = Intent(requireContext(), HelpActivity::class.java)
            startActivity(intent)
        }

        // Tombol hapus semua transaksi
        binding.btnDeleteAll.setOnClickListener {
            deleteAllTransaction()
        }
    }

    private fun deleteAllTransaction() {
        viewLifecycleOwner.lifecycleScope.launch {
            val userPreference = UserPreference.getInstance(requireContext().dataStore)
            val session = userPreference.getSession().first()

            val userId = session.userId
            val token = session.token

            try {
                val response = ApiConfig.getApiService(token).deleteAllTransaction(userId)
                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        showToast(response.body()?.message ?: "Berhasil menghapus semua transaksi.")
                    } else {
                        showToast(response.body()?.message ?: "Gagal menghapus transaksi.")
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    showToast("Terjadi kesalahan: ${e.message}")
                }
            }
        }
    }

    private fun showToast(message: String) {
        if (isAdded) {
            Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
