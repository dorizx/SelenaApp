package com.example.selenaapp.ui.help

import android.os.Bundle
import android.widget.ExpandableListView
import androidx.appcompat.app.AppCompatActivity
import com.example.selenaapp.R

class HelpActivity : AppCompatActivity() {

    private lateinit var expandableListView: ExpandableListView
    private lateinit var expandableListAdapter: ExpandableListAdapter
    private lateinit var questionList: List<String>  // Pertanyaan
    private lateinit var answerList: List<List<Pair<String, Int?>>>
    // Jawaban

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_help)

        expandableListView = findViewById(R.id.expandableListView)

        // Data pertanyaan dan jawaban
        questionList = listOf(
            "Apa itu SelenaApp?",
            "Bagaimana cara menggunakan aplikasi?",
            "Bagaimana Cara Mengunduh File Excel Tokopedia?"
        )
        answerList = listOf(
            listOf(Pair("SelenaApp adalah aplikasi yang menyediakan berbagai fitur untuk manajemen transaksi.", null)),
            listOf(Pair("Untuk menggunakan aplikasi, Anda perlu melakukan login terlebih dahulu, lalu pilih menu yang sesuai.", null)),
            listOf(
                Pair("Buka Tokopedia Seller. Klik menu Pesanan pada sidebar kiri.", R.drawable.tokopedia_step_1),
                Pair("Pilih tab Pesanan Selesai untuk memfilter pesanan yang sudah selesai.", R.drawable.tokopedia_step_2),
                Pair("Klik tombol Download Laporan di kanan atas.", R.drawable.tokopedia_step_3),
                Pair("Tentukan rentang waktu laporan dalam kotak dialog.", R.drawable.tokopedia_step_4),
                Pair("Klik tombol Minta Laporan setelah memilih rentang waktu.", R.drawable.tokopedia_step_5),
                Pair("Pindah ke tab Riwayat Laporan untuk mengunduh laporan.", R.drawable.tokopedia_step_6),
                Pair("Cari laporan yang baru saja diminta berdasarkan tanggal, lalu klik tombol Download di sebelahnya (gambar ketiga).", R.drawable.tokopedia_step_7)
            )
        )

        // Menggunakan adapter
        expandableListAdapter = ExpandableListAdapter(this, questionList, answerList)
        expandableListView.setAdapter(expandableListAdapter)
    }
}
