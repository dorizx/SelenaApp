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

        homeViewModel.incomeTransationsSize.observe(viewLifecycleOwner) { incomeTransationsSize ->
            val totalIncome = homeViewModel.totalIncome.value ?: 0

            if (incomeTransationsSize != 0) {
                val averageIncome = totalIncome / incomeTransationsSize
                val formattedAverageIncome = NumberFormat.getCurrencyInstance(Locale("in", "ID")).format(averageIncome)
                binding.valueAverageIncome.text = formattedAverageIncome
            } else {
                binding.valueAverageIncome.text = "Rp 0"
            }
        }

        homeViewModel.expenseTransationsSize.observe(viewLifecycleOwner) { expenseTransationsSize ->
            val totalExpense = homeViewModel.totalExpense.value?: 0

            if (expenseTransationsSize != 0) {
                val averageExpense = totalExpense / expenseTransationsSize
                val formattedAverageExpense = NumberFormat.getCurrencyInstance(Locale("in", "ID")).format(averageExpense)
                binding.valueAverageExpense.text = formattedAverageExpense
            } else {
                binding.valueAverageExpense.text = "Rp 0"
            }
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

        val total = totalIncome + totalExpense
        val totalIncomePercentage = if (total > 0) (totalIncome / total) * 100 else 0f
        val totalExpensePercentage = if (total > 0) (totalExpense / total) * 100 else 0f


        val pieEntries = listOf(
            PieEntry(totalIncomePercentage, "Pemasukan"),
            PieEntry(totalExpensePercentage, "Pengeluaran")
        )

        if (pieEntries.isEmpty()) {
            binding.pieChart.visibility = View.GONE
        }



        val pieDataSet = PieDataSet(pieEntries, "Persentase Keuangan")
        pieDataSet.colors = listOf(
            ContextCompat.getColor(requireContext(), R.color.green),
            ContextCompat.getColor(requireContext(), R.color.red)
        )


        val pieData = PieData(pieDataSet)
        binding.pieChart.data = pieData
        binding.pieChart.description.isEnabled = false
        binding.pieChart.setUsePercentValues(true)
        binding.pieChart.invalidate()
        binding.pieChart.setEntryLabelColor(ContextCompat.getColor(requireContext(), R.color.black))
        pieDataSet.valueTextSize = 13f
        binding.pieChart.legend.apply {
            textColor = ContextCompat.getColor(requireContext(), R.color.main_light)
            textSize = 12f
            xEntrySpace = 16f
        }
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
