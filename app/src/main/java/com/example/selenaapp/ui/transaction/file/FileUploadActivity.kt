package com.example.selenaapp.ui.transaction.file

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.selenaapp.R
import com.example.selenaapp.databinding.ActivityFileUploadBinding

class FileUploadActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFileUploadBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFileUploadBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (savedInstanceState == null) {
            val fragment = ChooseFileFragment()
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit()
        }

    }
}