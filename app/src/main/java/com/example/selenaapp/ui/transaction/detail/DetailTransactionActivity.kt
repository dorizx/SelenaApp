package com.example.selenaapp.ui.transaction.detail

import android.content.ContentValues.TAG
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.DocumentsContract
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.lifecycle.lifecycleScope
import com.example.selenaapp.data.api.ApiConfig
import com.example.selenaapp.data.preference.UserPreference
import com.example.selenaapp.data.preference.dataStore
import com.example.selenaapp.data.response.DataItem
import com.example.selenaapp.databinding.ActivityDetailTransactionBinding
import com.example.selenaapp.ui.main.MainActivity
import com.example.selenaapp.ui.transaction.TransactionFragment
import com.example.selenaapp.ui.transaction.update.UpdateTransactionActivity
import com.itextpdf.kernel.pdf.PdfName.Font
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone
import com.itextpdf.kernel.pdf.PdfDocument
import com.itextpdf.kernel.pdf.PdfWriter
import com.itextpdf.layout.Document
import com.itextpdf.layout.element.Paragraph
import com.itextpdf.layout.element.Text
import com.itextpdf.layout.property.TextAlignment
import com.itextpdf.layout.property.VerticalAlignment
import com.itextpdf.kernel.font.PdfFontFactory
import com.itextpdf.kernel.font.PdfFont
import com.itextpdf.kernel.pdf.PdfName


class DetailTransactionActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailTransactionBinding

    companion object {
        const val EXTRA_TRANSACTION_ID = "extra_transaction_id"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailTransactionBinding.inflate(layoutInflater)
        setContentView(binding.root)


        val transaction = intent.getParcelableExtra<DataItem>(EXTRA_TRANSACTION_ID)
//        if (transaction != null) {
//            val rupiahFormatter = NumberFormat.getCurrencyInstance(Locale("in", "ID"))
//            val amount = rupiahFormatter.format(transaction.amount)
//            binding.tvAmount.text = amount
//            binding.tvCreatedAt.text = transaction.date.toString()
//            binding.tvTransactionIDValue.text = transaction.transactionId.toString()
//            binding.tvDateValue.text = transaction.date.toString()
//            binding.tvStatusValue.text = transaction.transactionType.toString()
//            binding.tvNotesValue.text = transaction.catatan.toString()
//        }

        getDetail()

        binding.btnDelete.setOnClickListener {
            deleteTransaction(transaction?.transactionId!!.toInt())
        }

        binding.btnUpdate.setOnClickListener {
            val intent = Intent(this, UpdateTransactionActivity::class.java)
            intent.putExtra(EXTRA_TRANSACTION_ID, transaction)
            startActivity(intent)
        }

        binding.btnDownloadPdf.setOnClickListener {
            val transaction = intent.getParcelableExtra<DataItem>(EXTRA_TRANSACTION_ID)
            if (transaction != null) {
                createPdf(transaction)
            }
        }

    }

    private fun getDetail() {
        lifecycleScope.launch {
            val userPreference = UserPreference.getInstance(dataStore)
            val userModel = userPreference.getSession().first()
            val token = userModel.token
            Log.d(TAG, "getDetail: $token")
            val transaction = intent.getParcelableExtra<DataItem>(EXTRA_TRANSACTION_ID)
            val transactionId = transaction?.transactionId
            binding.tvTypeTransaction.text = transaction?.transactionType.toString()


            if (transactionId != null) {
                val response = ApiConfig.getApiService(token).getDetailTransaction(transactionId)
                if (response.isSuccessful) {
                    val data = response.body()
                    if (data != null) {
                        val transaction = data.data
                        val rupiahFormatter = NumberFormat.getCurrencyInstance(Locale("in", "ID"))
                        val amount = rupiahFormatter.format(transaction?.amount)
                        Log.d(TAG, "getDetail: ${transaction?.userId}")
                        Log.d(TAG, "getDetail: ${transaction?.transactionId}")
                        Log.d(TAG, "getDetail: ${transaction?.amount}")
                        binding.tvAmount.text = amount

                        binding.tvUserIdValue.text = transaction?.userId.toString()
                        binding.tvTransactionIDValue.text = transaction?.transactionId.toString()
                        binding.tvDateValue.text = transaction?.date.toString()
                        binding.tvStatusValue.text = transaction?.status.toString()
                        binding.tvNotesValue.text = transaction?.catatan.toString()
                        binding.tvCreatedAtValue.text =
                            transaction?.createdAt?.let { formatDate(it) } ?: "N/A"
                        binding.tvUpdatedAtValue.text =
                            transaction?.updatedAt?.let { formatDate(it) } ?: "N/A"
                    }
                }
            }
        }
    }

    private fun formatDate(dateString: String): String {
        return try {
            // Format input sesuai dengan format ISO 8601
            val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
            inputFormat.timeZone = TimeZone.getTimeZone("UTC")

            // Format output sesuai keinginan
            val outputFormat = SimpleDateFormat("dd-MM-yyyy, HH:mm:ss", Locale.getDefault())
            outputFormat.timeZone = TimeZone.getDefault()

            val date = inputFormat.parse(dateString)
            if (date != null) {
                outputFormat.format(date)
            } else {
                dateString // Kembalikan string asli jika parsing gagal
            }
        } catch (e: Exception) {
            dateString // Kembalikan string asli jika ada error
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
                                this@DetailTransactionActivity,
                                "Transaksi berhasil dihapus",
                                Toast.LENGTH_SHORT
                            ).show()
                            navigateBackToTransactions()
                        } else {
                            Toast.makeText(
                                this@DetailTransactionActivity,
                                "Gagal menghapus transaksi",
                                Toast.LENGTH_SHORT
                            ).show()
                            navigateBackToTransactions()
                        }
                    } catch (e: Exception) {
                        Toast.makeText(
                            this@DetailTransactionActivity,
                            "Error: ${e.message}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } else {
                    Toast.makeText(
                        this@DetailTransactionActivity,
                        "Token tidak valid. Silakan login kembali.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    private fun navigateBackToTransactions() {
        finish()
    }

    private fun createPdf(transaction: DataItem) {
        try {
            // Menentukan lokasi file PDF
            val file = File(filesDir, "transaction_${transaction.transactionId}.pdf")

            // Membuat PdfWriter dengan FileOutputStream
            val pdfWriter = PdfWriter(FileOutputStream(file))
            val pdfDocument = PdfDocument(pdfWriter)
            val document = Document(pdfDocument)

            // Menambahkan konten ke dalam dokumen PDF
            val titleFont: PdfFont = PdfFontFactory.createFont("Times-Roman")
            val contentFont: PdfFont = PdfFontFactory.createFont("Helvetica")

            document.add(Paragraph("Transaction Details").setFont(titleFont).setFontSize(18f))
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
            // Log error untuk debugging
            Log.e(TAG, "Failed to create PDF", e)

            // Menampilkan Toast error yang lebih jelas
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
}