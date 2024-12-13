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
            "Bagaimana Cara Mengunduh File Excel Tokopedia?",
            "Bagaimana Cara Mengunduh File Excel Shopee?"
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
                Pair("Cari laporan yang baru saja diminta berdasarkan tanggal, lalu klik tombol Download di sebelahnya .", R.drawable.tokopedia_step_7),
                Pair("Ikuti langkah-langkah ini untuk memastikan laporan yang benar diunduh dan dapat digunakan sesuai kebutuhan. Jika ada kendala, pastikan Anda menggunakan browser terbaru dan memperbarui halaman.", null)
            ),
            listOf(
                Pair("Buka Shopee Seller Center. Klik menu Saldo Saya di sidebar kiri", R.drawable.shopee_step_1),
                Pair("Pada halaman Saldo Penjual, pilih rentang tanggal transaksi yang diinginkan dengan membuka dropdown Tanggal Transaksi Dibuat. Pilih opsi seperti \"Dalam bulan ini,\" \"3 bulan terakhir,\" atau tetapkan tanggal khusus (gambar keempat).", R.drawable.shopee_step_2),
                Pair("Filter tipe transaksi ke \"Transaksi Masuk\" untuk mengimpor data pendapatan. Hal ini harus dilakukan agar pengunggahan file Excel berhasil, karena Selena hanya menerima data pendapatan dalam kasus in", R.drawable.shopee_step_3),
                Pair("Setelah menentukan rentang tanggal dan filter, klik tombol Terapkan untuk menampilkan hasil yang sesuai .", R.drawable.shopee_step_4),
                Pair("Di bagian kanan bawah tabel transaksi, klik tombol Export untuk memulai proses pembuatan laporan Excel.", R.drawable.shopee_step_5),
                Pair("Setelah laporan siap, buka bagian Laporan Terakhir yang muncul di sebelah kanan layar. Klik tombol Download untuk mengunduh file laporan.", R.drawable.shopee_step_6),
                Pair("Panduan ini akan membantu Anda menghasilkan file laporan transaksi Shopee yang akurat untuk kebutuhan pencatatan atau pengelolaan bisnis Anda. Jika terdapat kendala, pastikan browser Anda dalam versi terbaru dan coba segarkan halaman.", null)
            )
        )

        // Menggunakan adapter
        expandableListAdapter = ExpandableListAdapter(this, questionList, answerList)
        expandableListView.setAdapter(expandableListAdapter)
    }
}
