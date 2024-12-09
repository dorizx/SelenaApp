package com.example.selenaapp.ui.home

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.selenaapp.R
import com.example.selenaapp.data.api.ApiConfig
import com.example.selenaapp.data.preference.UserPreference
import com.example.selenaapp.data.preference.dataStore
import com.example.selenaapp.databinding.FragmentHomeBinding
import com.example.selenaapp.ui.transaction.TransactionAdapter
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.util.Locale

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private lateinit var adapter: DashboardAdapter

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val homeViewModel =
            ViewModelProvider(this).get(HomeViewModel::class.java)

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        showLoading(false)

        binding.recyclerViewAnomaly.layoutManager = LinearLayoutManager(
            requireContext(),
            LinearLayoutManager.HORIZONTAL, false
        )

        getAnomaly()

    }

    //@SuppressLint("SuspiciousIndentation")
    private fun getAnomaly() {
        showLoading(true)
        val context = requireContext()
        viewLifecycleOwner.lifecycleScope.launch {
            val userPreference = UserPreference.getInstance(context.dataStore)
            val userModel = userPreference.getSession().first()
            val token = userModel.token
            val userId = userModel.userId
            try {
                val response = ApiConfig.getApiService(token)
                    .getDashboard(userId)

                if (response.isSuccessful) {
                    val transactions = response.body()?.anomalyTransactions ?: emptyList()
                    binding.tvFinanceAdvice.text = response.body()?.financialAdvice

                    val totalIncome = response.body()?.totalIncome?.toFloat() ?: 0f
                    val totalExpense = response.body()?.totalExpense?.toFloat() ?: 0f
                    val mountedIncome =transactions.size
                    val averageIncome = totalIncome/mountedIncome

                    Log.d("DASHBOARDDDDDDDD", "totalIncome: $totalIncome")

                    setupPieChart(totalIncome, totalExpense)

                    val rupiahFormatter = NumberFormat.getCurrencyInstance(Locale("in", "ID"))

                    val formattedIncome = rupiahFormatter.format(totalIncome)
                    val formattedExpense = rupiahFormatter.format(totalExpense)
                    val formattedProfit = rupiahFormatter.format(averageIncome)

                    binding.valueDataIncome.text = formattedIncome
                    binding.valueDataExpense.text = formattedExpense
                    binding.valueDataProfit.text = formattedProfit

                    viewLifecycleOwner.lifecycleScope.launch(Dispatchers.Main) {
                        adapter = DashboardAdapter(transactions)
                        binding.recyclerViewAnomaly.adapter = adapter
                        handleEmptyState(transactions.isEmpty())
                        showLoading(false)
                    }
                } else {
                    //Toast.makeText(context, response.message(), Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                //Toast.makeText(context, e.message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setupPieChart(totalIncome: Float, totalExpense: Float) {
        val pieEntries = listOf(
            PieEntry(totalIncome, "Income"),
            PieEntry(totalExpense, "Expense")
        )

        val pieDataSet = PieDataSet(pieEntries, "Persentase Income dan Expense")
        pieDataSet.colors = listOf(
            ContextCompat.getColor(requireContext(), R.color.button_1_dark),
            ContextCompat.getColor(requireContext(), R.color.button_2_dark)
        )
        pieDataSet.valueTextColor = ContextCompat.getColor(requireContext(), android.R.color.black)
        pieDataSet.valueTextSize = 12f

        val pieData = PieData(pieDataSet)
        binding.pieChart.data = pieData
        binding.pieChart.description.isEnabled = false
        binding.pieChart.setUsePercentValues(true)
        binding.pieChart.invalidate() // Refresh chart
    }


    private fun handleEmptyState(isEmpty: Boolean) {
        binding.recyclerViewAnomaly.visibility = if (isEmpty) View.GONE else View.VISIBLE
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressIndicator.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}