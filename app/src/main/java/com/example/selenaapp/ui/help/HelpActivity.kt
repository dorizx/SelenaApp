package com.example.selenaapp.ui.help

import android.os.Bundle
import android.widget.ExpandableListView
import androidx.appcompat.app.AppCompatActivity
import com.example.selenaapp.R

class HelpActivity : AppCompatActivity() {

    private lateinit var expandableListView: ExpandableListView
    private lateinit var expandableListAdapter: ExpandableListAdapter
    private lateinit var questionList: List<String>  // Pertanyaan
    private lateinit var answerList: List<List<String>>  // Jawaban

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_help)

        expandableListView = findViewById(R.id.expandableListView)

        // Data pertanyaan dan jawaban
        questionList = listOf("Apa itu SelenaApp?", "Bagaimana cara menggunakan aplikasi?", "Bagaimana cara reset password?")
        answerList = listOf(
            listOf("SelenaApp adalah aplikasi yang menyediakan berbagai fitur untuk manajemen transaksi."),
            listOf("Untuk menggunakan aplikasi, Anda perlu melakukan login terlebih dahulu, lalu pilih menu yang sesuai."),
            listOf("Untuk reset password, pergi ke halaman login dan klik 'Lupa Password'. Ikuti petunjuk untuk mereset.")
        )

        // Menggunakan adapter
        expandableListAdapter = ExpandableListAdapter(this, questionList, answerList)
        expandableListView.setAdapter(expandableListAdapter)
    }
}
