package com.example.selenaapp

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.ViewModelProvider
import com.example.selenaapp.data.preference.dataStore
import com.example.selenaapp.ui.main.MainActivity
import com.example.selenaapp.ui.settings.SettingsPreference
import com.example.selenaapp.ui.settings.SettingsViewModel
import com.example.selenaapp.ui.settings.SettingsViewModelFactory

class SplashScreenActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)
        saveTheme()
        supportActionBar?.hide()

        val handler = Handler()
        handler.postDelayed({
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }, 2500)

    }

    fun saveTheme() {
        val pref = SettingsPreference.getInstance(applicationContext.dataStore)

        // Gunakan ViewModel untuk memantau pengaturan tema
        val viewModelFactory = SettingsViewModelFactory(pref)
        val settingsViewModel = ViewModelProvider(this, viewModelFactory).get(SettingsViewModel::class.java)

        // Dapatkan pengaturan tema dari DataStore dan atur tema aplikasi
        settingsViewModel.getThemeSettings().observe(this) { isDarkModeActive ->
            Log.d("MainActivity", "isDarkModeActive: $isDarkModeActive")
            if (isDarkModeActive) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }
        }
    }


}