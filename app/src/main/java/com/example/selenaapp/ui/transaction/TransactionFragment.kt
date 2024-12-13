package com.example.selenaapp.ui.transaction

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.selenaapp.ViewModelFactory
import com.example.selenaapp.data.injection.Injection
import com.example.selenaapp.data.preference.UserPreference
import com.example.selenaapp.data.preference.dataStore
import com.example.selenaapp.data.repository.HomeRepository
import com.example.selenaapp.databinding.FragmentTransactionBinding

class TransactionFragment : Fragment() {

    private var _binding: FragmentTransactionBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: TransactionViewModel
    private lateinit var adapter: TransactionAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTransactionBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as? AppCompatActivity)?.supportActionBar?.hide()

        val userPreference = UserPreference.getInstance(requireContext().dataStore)
        val userRepository = Injection.provideUserRepository(requireContext())
        val viewModelFactory = ViewModelFactory(userRepository, userPreference, homeRepository = HomeRepository(userPreference))
        viewModel = ViewModelProvider(this, viewModelFactory)[TransactionViewModel::class.java]

        setupObservers()
        setupUI()
    }

    private fun setupUI() {
        binding.recyclerViewTransaksi.layoutManager = LinearLayoutManager(requireContext())

        viewModel.fetchTransactions()

        binding.refreshButton.setOnClickListener {
            viewModel.fetchTransactions()
        }

        binding.buttonAddTransaction.setOnClickListener {
            val intent = Intent(requireContext(), ChooseMethodTransactionActivity::class.java)
            startActivity(intent)
        }
    }

    private fun setupObservers() {
        viewModel.transactions.observe(viewLifecycleOwner) { transactions ->
            if (transactions.isNullOrEmpty()) {
                handleEmptyState(true)
            } else {
                handleEmptyState(false)
            }

            if (::adapter.isInitialized) {
                adapter.updateData(transactions)
            } else {
                adapter = TransactionAdapter(transactions)
                binding.recyclerViewTransaksi.adapter = adapter
            }
        }

        viewModel.totalIncome.observe(viewLifecycleOwner) { income ->
            binding.tvIncome.text = income
        }

        viewModel.totalExpense.observe(viewLifecycleOwner) { expense ->
            binding.tvOutcome.text = expense
        }

        viewModel.totalProfit.observe(viewLifecycleOwner) { profit ->
            binding.tvProfit.text = profit
        }

        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.progressIndicator.visibility = if (isLoading) View.VISIBLE else View.GONE
        }

        viewModel.errorMessage.observe(viewLifecycleOwner) { message ->
            Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
        }
    }

    private fun handleEmptyState(isEmpty: Boolean) {
        binding.recyclerViewTransaksi.visibility = if (isEmpty) View.GONE else View.VISIBLE
        binding.textStatus.visibility = if (isEmpty) View.VISIBLE else View.GONE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}