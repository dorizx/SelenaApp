package com.example.selenaapp.ui.home

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.selenaapp.data.api.ApiConfig
import com.example.selenaapp.data.preference.UserPreference
import com.example.selenaapp.data.preference.dataStore
import com.example.selenaapp.databinding.FragmentHomeBinding
import com.example.selenaapp.ui.transaction.TransactionAdapter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

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

        binding.recyclerViewAnomaly.layoutManager = LinearLayoutManager(requireContext())
        getAnomaly()

    }

    @SuppressLint("SuspiciousIndentation")
    private fun getAnomaly() {
        //showLoading(true)
        val context = requireContext()
        viewLifecycleOwner.lifecycleScope.launch{
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
                            viewLifecycleOwner.lifecycleScope.launch(Dispatchers.Main) {
                                adapter = DashboardAdapter(transactions)
                                binding.recyclerViewAnomaly.adapter = adapter
                                handleEmptyState(transactions.isEmpty())
                                //showLoading(false)
                        }
                    } else {
                        //Toast.makeText(context, response.message(), Toast.LENGTH_SHORT).show()
                    }
                } catch (e: Exception) {
                    //Toast.makeText(context, e.message, Toast.LENGTH_SHORT).show()
                }
        }
    }


    private fun handleEmptyState(isEmpty: Boolean) {
        binding.recyclerViewAnomaly.visibility = if (isEmpty) View.GONE else View.VISIBLE
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}