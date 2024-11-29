package com.example.selenaapp.ui.settings

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatDelegate
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.selenaapp.databinding.FragmentSettingsBinding
import com.example.selenaapp.ui.help.HelpActivity
import com.example.selenaapp.ui.login.LoginActivity

// Deklarasikan dataStore sebagai ekstensi Context
val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class SettingsFragment : Fragment() {

    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Pastikan ViewModel diinstansiasi menggunakan SettingsViewModelFactory
        val pref = SettingsPreference.getInstance(requireContext().dataStore)
        val viewModelFactory = SettingsViewModelFactory(pref)
        val mainViewModel = ViewModelProvider(this, viewModelFactory).get(SettingsViewModel::class.java)

        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        val root: View = binding.root

        // Tombol logout
        binding.btnLogout.setOnClickListener {
            val intent = Intent(requireContext(), LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }

        // Tombol help
        binding.cardHelp.setOnClickListener {
            val intent = Intent(requireContext(), HelpActivity::class.java)
            startActivity(intent)
        }

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

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
