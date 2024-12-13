package com.example.selenaapp.ui.home

import android.util.Log
import androidx.lifecycle.LiveData
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

    private val _totalIncome = MutableLiveData<Int>()
    val totalIncome: LiveData<Int> = _totalIncome

    private val _totalExpense = MutableLiveData<Int>()
    val totalExpense: LiveData<Int> = _totalExpense

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _financialAdvice = MutableLiveData<String>()
    val financialAdvice: LiveData<String> = _financialAdvice

    private val _incomeTransationsSize = MutableLiveData<Int>()
    val incomeTransationsSize: LiveData<Int> = _incomeTransationsSize

    private val _expenseTransationsSize = MutableLiveData<Int>()
    val expenseTransationsSize: LiveData<Int> = _expenseTransationsSize

    init {
        fetchAnomalyData()
    }

    fun fetchAnomalyData() {
        _isLoading.value = true
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val result = repository.getAnomalyTransactions()
                _anomalyTransactions.postValue(result.anomalyTransactions as List<AnomalyTransactionsItem>?)
                _totalIncome.postValue(result.transactionStats?.totalIncome?: 0 )
                _totalExpense.postValue(result.transactionStats?.totalExpense?: 0 )
                _financialAdvice.postValue(result.financialAdvice.toString())
                _incomeTransationsSize.postValue(result.transactionStats?.incomeTransationsSize ?: 0 )
                _expenseTransationsSize.postValue(result.transactionStats?.expenseTransationsSize ?: 0)
            } catch (e: Exception) {
                Log.e("HomeViewModel", "Error fetching anomaly data: ${e.message}")
            } finally {
                _isLoading.postValue(false)
            }
        }
    }


}
