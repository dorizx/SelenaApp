package com.example.selenaapp.ui.home

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.lifecycle.lifecycleScope
import com.example.selenaapp.data.api.ApiConfig
import com.example.selenaapp.data.preference.UserPreference
import com.example.selenaapp.data.preference.dataStore
import com.example.selenaapp.data.response.AnomalyTransactionsItem
import com.example.selenaapp.databinding.ActivityDetailAnomalyBinding
import com.itextpdf.kernel.font.PdfFont
import com.itextpdf.kernel.font.PdfFontFactory
import com.itextpdf.kernel.pdf.PdfDocument
import com.itextpdf.kernel.pdf.PdfWriter
import com.itextpdf.layout.Document
import com.itextpdf.layout.element.Paragraph
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.text.NumberFormat
import java.util.Locale

class DetailAnomalyActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailAnomalyBinding

    companion object {
        const val EXTRA_TRANSACTION_ANOMALY_ID = "extra_transaction_anomaly_id"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailAnomalyBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val transaction =
            intent.getParcelableExtra<AnomalyTransactionsItem>(EXTRA_TRANSACTION_ANOMALY_ID)
        val transactionId = transaction?.transactionId
        if (transaction != null) {
            val rupiahFormatter = NumberFormat.getCurrencyInstance(Locale("in", "ID"))
            val amount = rupiahFormatter.format(transaction.amount)
            binding.tvAmount.text = amount
            binding.tvTransactionIDValue.text = transaction.transactionId.toString()
            binding.tvDateValue.text = transaction.date.toString()
            binding.tvNotesValue.text = transaction.catatan.toString()
        }

        binding.btnDownloadPdf.setOnClickListener {
            if (transaction != null) {
                createPdf(transaction)
            }
        }

        binding.btnDelete.setOnClickListener {
            if (transactionId != null) {
                deleteTransaction(transactionId)
            }
        }
    }

    private fun createPdf(transaction: AnomalyTransactionsItem) {
        try {
            // Menentukan lokasi file PDF
            val file =
                File(filesDir, "Report Detail Anomaly Transaction_${transaction.transactionId}.pdf")

            val pdfWriter = PdfWriter(FileOutputStream(file))
            val pdfDocument = PdfDocument(pdfWriter)
            val document = Document(pdfDocument)

            val titleFont: PdfFont = PdfFontFactory.createFont("Times-Roman")
            val contentFont: PdfFont = PdfFontFactory.createFont("Helvetica")

            document.add(
                Paragraph("Transaction Anomaly Details").setFont(titleFont).setFontSize(18f)
            )
            document.add(
                Paragraph("Transaction ID: ${transaction.transactionId}").setFont(
                    contentFont
                ).setFontSize(12f)
            )
            document.add(
                Paragraph(
                    "Amount: ${
                        NumberFormat.getCurrencyInstance(Locale("in", "ID"))
                            .format(transaction.amount)
                    }"
                ).setFont(contentFont).setFontSize(12f)
            )
            document.add(
                Paragraph("Date: ${transaction.date}").setFont(contentFont).setFontSize(12f)
            )
            document.add(
                Paragraph("Notes: ${transaction.catatan}").setFont(contentFont).setFontSize(12f)
            )

            // Menutup dokumen PDF
            document.close()

            // Memberikan feedback bahwa PDF telah berhasil dibuat
            Toast.makeText(this, "PDF created successfully!", Toast.LENGTH_SHORT).show()

            // Optionally, membuka file PDF setelah pembuatan
            openPdf(file)

        } catch (e: Exception) {
            Toast.makeText(this, "Failed to create PDF: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }


    private fun openPdf(file: File) {
        try {
            // Menggunakan FileProvider untuk mendapatkan URI yang dapat dibagikan
            val uri = FileProvider.getUriForFile(
                this,
                "${packageName}.provider",
                file
            )

            // Membuat intent untuk membuka file PDF
            val intent = Intent(Intent.ACTION_VIEW)
            intent.setDataAndType(uri, "application/pdf")
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            startActivity(intent)
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this, "Failed to open PDF: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun deleteTransaction(transactionId: Int) {
        lifecycleScope.launch {
            val userPreference = UserPreference.getInstance(dataStore)
            userPreference.getSession().collect { userModel ->
                val token = userModel.token
                if (!token.isNullOrEmpty()) {
                    try {
                        val response = ApiConfig.getApiService(token)
                            .deleteTransaction(transactionId)
                        if (response.isSuccessful) {
                            Toast.makeText(
                                this@DetailAnomalyActivity,
                                "Transaksi berhasil dihapus",
                                Toast.LENGTH_SHORT
                            ).show()
                            finish()
                        } else {
                            Toast.makeText(
                                this@DetailAnomalyActivity,
                                "Gagal menghapus transaksi",
                                Toast.LENGTH_SHORT
                            ).show()
                            finish()
                        }
                    } catch (e: Exception) {
                        Toast.makeText(
                            this@DetailAnomalyActivity,
                            "Error: ${e.message}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } else {
                    Toast.makeText(
                        this@DetailAnomalyActivity,
                        "Token tidak valid. Silakan login kembali.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }
}