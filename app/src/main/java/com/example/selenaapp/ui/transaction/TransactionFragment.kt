package com.example.selenaapp.ui.transaction

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.selenaapp.data.api.ApiConfig
import com.example.selenaapp.data.preference.UserPreference
import com.example.selenaapp.data.preference.dataStore
import com.example.selenaapp.databinding.FragmentTransactionBinding
import com.example.selenaapp.ui.main.MainActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.util.Locale

class TransactionFragment : Fragment() {

    private var _binding: FragmentTransactionBinding? = null
    private lateinit var adapter: TransactionAdapter

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val transactionViewModel =
            ViewModelProvider(this).get(TransactionViewModel::class.java)

        _binding = FragmentTransactionBinding.inflate(inflater, container, false)
        val root: View = binding.root

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d("TransactionFragment", "onViewCreated called")

        showLoading(false)

        getTransactions()

        binding.recyclerViewTransaksi.layoutManager = LinearLayoutManager(requireContext())




        binding.buttonAddTransaction.setOnClickListener {
            val intent = Intent(requireContext(), ChooseMethodTransactionActivity::class.java)
            startActivity(intent)
        }
    }

    @SuppressLint("SuspiciousIndentation")
    private fun getTransactions() {
        showLoading(true)
        val context = requireContext()
        viewLifecycleOwner.lifecycleScope.launch{
            val userPreference = UserPreference.getInstance(context.dataStore)
            userPreference.getSession().collect { userModel ->
                val token = userModel.token
                val userId = userModel.userId
                try {
                    //mengambil data dari TransactionResponse
                    val response = ApiConfig.getApiService(token)
                        .getTransactions(userId)

                    if (response.isSuccessful) {
                        val transactions = response.body()?.data ?: emptyList()

                        //filter transaski yang income
                        val incomeTransactions = transactions.filter { it?.transactionType == "income" }
                        val expenseTransactions = transactions.filter { it?.transactionType == "expense" }

                        //menjumlahkan keseluruhan data transaksi income
                        val totalAllIncome = incomeTransactions.sumOf { it?.amount ?: 0 }
                        val totalAllExpense = expenseTransactions.sumOf { it?.amount ?: 0 }

                        //format perhitungan ke rupiah
                        val rupiahFormatter = NumberFormat.getCurrencyInstance(Locale("in", "ID"))

                        val totalProfit = totalAllIncome - totalAllExpense
                        val formattedIncome = rupiahFormatter.format(totalAllIncome)
                        val formattedExpense = rupiahFormatter.format(totalAllExpense)
                        val formattedProfit = rupiahFormatter.format(totalProfit)

                        viewLifecycleOwner.lifecycleScope.launch(Dispatchers.Main){
                            adapter = TransactionAdapter(transactions)
                            binding.recyclerViewTransaksi.adapter = adapter
                            binding.tvIncome.text = formattedIncome
                            binding.tvOutcome.text = formattedExpense
                            binding.tvProfit.text = formattedProfit
                            handleEmptyState(transactions.isEmpty())
                            showLoading(false)
                        }
                    } else {
                        handleEmptyState(true)
                        showToast("Gagal memuat data: ${response.errorBody()?.string()}")
                        showLoading(false)
                    }
                } catch (e: Exception) {
                    handleEmptyState(true)
                    showToast("Error: ${e.message}")
                    showLoading(false)
                }
            }
        }
    }

    private fun handleEmptyState(isEmpty: Boolean) {
        binding.recyclerViewTransaksi.visibility = if (isEmpty) View.GONE else View.VISIBLE
        binding.textStatus.visibility = if (isEmpty) View.VISIBLE else View.GONE
    }

    private fun showToast(message: String) {
        if (isAdded) {
            CoroutineScope(Dispatchers.Main).launch {
                Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressIndicator.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        Log.d("TransactionFragment", "onDestroyView called")
        _binding = null
    }
}