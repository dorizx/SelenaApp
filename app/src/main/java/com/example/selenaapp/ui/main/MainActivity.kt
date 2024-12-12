package com.example.selenaapp.ui.main

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.selenaapp.R
import com.example.selenaapp.ViewModelFactory
import com.example.selenaapp.data.preference.dataStore
import com.example.selenaapp.databinding.ActivityMainBinding
import com.example.selenaapp.ui.login.LoginActivity
import com.example.selenaapp.ui.settings.SettingsPreference
import com.example.selenaapp.ui.settings.SettingsViewModel
import com.example.selenaapp.ui.settings.SettingsViewModelFactory
import kotlinx.coroutines.launch


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var settingsPreferences: SettingsPreference
    private val viewModel by viewModels<MainViewModel> {
        ViewModelFactory.getInstance(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel.getSession().observe(this) { user ->
            if (!user.isLogin) {
                startActivity(Intent(this, LoginActivity::class.java))
                finish()
            }
        }

        settingsPreferences = SettingsPreference.getInstance(dataStore)
        // Check and apply theme only once in onCreate
        checkThemeSettings()

        val navView: BottomNavigationView = binding.navView
        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        val appBarConfiguration = AppBarConfiguration(
            setOf(R.id.navigation_home, R.id.navigation_transaction, R.id.navigation_settings)
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        intent.getStringExtra("navigate_to")?.let { destination ->
            if (destination == "transactions") {
                navController.navigate(R.id.navigation_transaction)
            }
        }
    }

    // Apply theme only once, avoiding infinite loop
    private fun checkThemeSettings() {
        lifecycleScope.launch {
            settingsPreferences.getThemeSetting().collect { isDarkModeActive ->
                // Apply theme only if needed
                if (isDarkModeActive) {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                } else {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                }
            }
        }
    }
}


