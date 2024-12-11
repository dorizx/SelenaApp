package com.example.selenaapp.ui.home

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.selenaapp.data.repository.HomeRepository
import com.example.selenaapp.data.response.AnomalyTransactionsItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class HomeViewModel(private val repository: HomeRepository) : ViewModel() {

    private val _anomalyTransactions = MutableLiveData<List<AnomalyTransactionsItem>?>()
    val anomalyTransactions: LiveData<List<AnomalyTransactionsItem>?> = _anomalyTransactions

    private val _totalIncome = MutableLiveData<String>()
    val totalIncome: LiveData<String> = _totalIncome

    private val _totalExpense = MutableLiveData<String>()
    val totalExpense: LiveData<String> = _totalExpense

    private val _totalProfit = MutableLiveData<String>()
    val totalProfit: LiveData<String> = _totalProfit

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _financialAdvice = MutableLiveData<String>()
    val financialAdvice: LiveData<String> = _financialAdvice

    init {
        fetchAnomalyData()
    }

    fun fetchAnomalyData() {
        _isLoading.value = true
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val result = repository.getAnomalyTransactions()
                _anomalyTransactions.postValue(result.anomalyTransactions as List<AnomalyTransactionsItem>?)
                _totalIncome.postValue(result.totalIncome?.toString() ?: "0")
                _totalExpense.postValue(result.totalExpense?.toString() ?: "0")
                _financialAdvice.postValue(result.financialAdvice.toString())
            } catch (e: Exception) {
                Log.e("HomeViewModel", "Error fetching anomaly data: ${e.message}")
            } finally {
                _isLoading.postValue(false)
            }
        }
    }


}
