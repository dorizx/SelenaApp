package com.example.selenaapp.ui.settings

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.selenaapp.data.api.ApiConfig
import com.example.selenaapp.data.preference.UserPreference
import com.example.selenaapp.data.preference.dataStore
import com.example.selenaapp.databinding.FragmentSettingsBinding
import com.example.selenaapp.ui.help.HelpActivity
import com.example.selenaapp.ui.login.LoginActivity
import com.example.selenaapp.ui.main.MainActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

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
        (activity as? AppCompatActivity)?.supportActionBar?.hide()

        val pref = SettingsPreference.getInstance(requireContext().dataStore)
        val viewModelFactory = SettingsViewModelFactory(pref)
        val settingViewModel = ViewModelProvider(this, viewModelFactory).get(SettingsViewModel::class.java)

        // Observe changes to theme setting
        settingViewModel.getThemeSettings().observe(viewLifecycleOwner) { isDarkModeActive ->
            binding.switch2.isChecked = isDarkModeActive
        }

        binding.switch2.setOnCheckedChangeListener { _, isChecked ->
            settingViewModel.saveThemeSetting(isChecked)
        }

        setupUserInfo()
        setupListeners()
    }

    private fun setupUserInfo() {
        viewLifecycleOwner.lifecycleScope.launch {
            val userPreference = UserPreference.getInstance(requireContext().dataStore)
            val userModel = userPreference.getSession().first()
            binding.tvUser.text = userModel.name
            binding.tvEmail.text = userModel.email
        }
    }

    private fun setupListeners() {
        // Tombol logout
        binding.btnLogout.setOnClickListener {
            AlertDialog.Builder(requireContext()).apply {
            setTitle("Keluar")
            setMessage("Anda yakin ingin keluar aplikasi?")
            setPositiveButton("Ya") { _, _ ->
                viewLifecycleOwner.lifecycleScope.launch {
                    val userPreference = UserPreference.getInstance(requireContext().dataStore)
                    userPreference.logout()
                    val intent = Intent(requireContext(), LoginActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                }
            }
            setNegativeButton("Tidak") { _, _ ->
                //do nothing
            }
            create()
            show()
        }

        }

        // Tombol help
        binding.cardHelp.setOnClickListener {
            val intent = Intent(requireContext(), HelpActivity::class.java)
            startActivity(intent)
        }

        // Tombol hapus semua transaksi
        binding.btnDeleteAll.setOnClickListener {
            AlertDialog.Builder(requireContext()).apply {
                setTitle("Hapus Semua Transaksi")
                setMessage("Anda yakin ingin menghapus semua transaksi?")
                setPositiveButton("Ya") { _, _ ->
                    deleteAllTransaction()
                }
                setNegativeButton("Tidak") { _, _ ->
                    //do nothing
                }
                create()
                show()
            }
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
                        val intent = Intent(context, MainActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        startActivity(intent)
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





