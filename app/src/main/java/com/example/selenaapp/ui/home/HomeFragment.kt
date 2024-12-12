package com.example.selenaapp.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.selenaapp.R
import com.example.selenaapp.ViewModelFactory
import com.example.selenaapp.data.api.ApiConfig
import com.example.selenaapp.data.injection.Injection
import com.example.selenaapp.data.preference.UserPreference
import com.example.selenaapp.data.preference.dataStore
import com.example.selenaapp.data.repository.HomeRepository
import com.example.selenaapp.databinding.FragmentHomeBinding
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import java.text.NumberFormat
import java.util.Locale

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private lateinit var homeViewModel: HomeViewModel

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as? AppCompatActivity)?.supportActionBar?.hide()

        val userPreference = UserPreference.getInstance(requireContext().dataStore)
        val userRepository = Injection.provideUserRepository(requireContext())
        val viewModelFactory = ViewModelFactory(userRepository, userPreference, HomeRepository(userPreference))
        homeViewModel = ViewModelProvider(this, viewModelFactory).get(HomeViewModel::class.java)



        observeViewModel()
        setupRecyclerView()
        setupUi()

    }

    private fun setupUi() {
        binding.refreshButton.setOnClickListener {
            homeViewModel.fetchAnomalyData()
        }
    }

    private fun observeViewModel() {
        homeViewModel.anomalyTransactions.observe(viewLifecycleOwner) { transactions ->
            binding.recyclerViewAnomaly.adapter = transactions?.let { DashboardAdapter(it) }
            if (transactions != null) {
                handleEmptyState(transactions.isEmpty())
            }
        }

        homeViewModel.financialAdvice.observe(viewLifecycleOwner) { financialAdvice ->
            binding.tvFinanceAdvice.text = financialAdvice
        }

        homeViewModel.totalIncome.observe(viewLifecycleOwner) { totalIncome ->
            val formattedIncome = NumberFormat.getCurrencyInstance(Locale("in", "ID")).format(totalIncome.toInt())
            binding.valueDataIncome.text = formattedIncome
        }

        homeViewModel.totalExpense.observe(viewLifecycleOwner) { totalExpense ->
            val formattedExpense = NumberFormat.getCurrencyInstance(Locale("in", "ID")).format(totalExpense.toInt())
            binding.valueDataExpense.text = formattedExpense
        }

        homeViewModel.totalProfit.observe(viewLifecycleOwner) { totalProfit ->
            binding.valueDataProfit.text = totalProfit
        }

        homeViewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            showLoading(isLoading)
        }

        homeViewModel.totalIncome.observe(viewLifecycleOwner) { totalIncome ->
            val totalIncomeFloat = totalIncome.toFloat()
            homeViewModel.totalExpense.observe(viewLifecycleOwner) { totalExpense ->
                setupPieChart(totalIncomeFloat, totalExpense.toFloat())
            }
        }
    }

    private fun setupRecyclerView() {
        binding.recyclerViewAnomaly.layoutManager = LinearLayoutManager(
            requireContext(), LinearLayoutManager.HORIZONTAL, false
        )
    }

    private fun setupPieChart(totalIncome: Float, totalExpense: Float) {
        val pieEntries = listOf(
            PieEntry(totalIncome, "Pemasukan"),
            PieEntry(totalExpense, "Pengeluaran")
        )

        if (pieEntries.isEmpty()) {
            binding.pieChart.visibility = View.GONE
        }

        val pieDataSet = PieDataSet(pieEntries, "Persentase Pemasukan dan Pengeluaran")
        pieDataSet.colors = listOf(
            ContextCompat.getColor(requireContext(), R.color.green),
            ContextCompat.getColor(requireContext(), R.color.red)
        )
        pieDataSet.valueTextColor = ContextCompat.getColor(requireContext(), android.R.color.black)
        pieDataSet.valueTextSize = 12f

        val pieData = PieData(pieDataSet)
        binding.pieChart.data = pieData
        binding.pieChart.description.isEnabled = false
        binding.pieChart.setUsePercentValues(true)
        binding.pieChart.invalidate()
    }


    private fun showLoading(isLoading: Boolean) {
        binding.progressIndicator.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    private fun handleEmptyState(isEmpty: Boolean) {
        binding.recyclerViewAnomaly.visibility = if (isEmpty) View.GONE else View.VISIBLE
        binding.tvAnomaly.visibility = if (isEmpty) View.VISIBLE else View.GONE
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
