package com.example.selenaapp.ui.transaction

import android.util.Log
import androidx.lifecycle.*
import com.example.selenaapp.data.api.ApiConfig
import com.example.selenaapp.data.preference.UserPreference
import com.example.selenaapp.data.response.DataItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.util.*

class TransactionViewModel(private val userPreference: UserPreference) : ViewModel() {

    private val _transactions = MutableLiveData<List<DataItem?>>()
    val transactions: LiveData<List<DataItem?>> = _transactions

    private val _totalIncome = MutableLiveData<String>()
    val totalIncome: LiveData<String> = _totalIncome

    private val _totalExpense = MutableLiveData<String>()
    val totalExpense: LiveData<String> = _totalExpense

    private val _totalProfit = MutableLiveData<String>()
    val totalProfit: LiveData<String> = _totalProfit

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> = _errorMessage

    fun fetchTransactions() {
        _isLoading.value = true
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val user = userPreference.getSession().first()
                val token = user.token
                Log.d("TransactionViewModel", "fetchTransactions: $token")
                val response = ApiConfig.getApiService(user.token).getTransactions(user.userId)

                if (response.isSuccessful || response.body() != null) {
                    val transactions = response.body()?.data ?: emptyList()
                    _transactions.postValue(transactions)

                    val incomeTransactions = transactions.filter { it?.transactionType == "income" }
                    val expenseTransactions = transactions.filter { it?.transactionType == "expense" }

                    val totalIncome = incomeTransactions.sumOf { it?.amount ?: 0 }
                    val totalExpense = expenseTransactions.sumOf { it?.amount ?: 0 }
                    val totalProfit = totalIncome - totalExpense

                    val rupiahFormatter = NumberFormat.getCurrencyInstance(Locale("in", "ID"))

                    _totalIncome.postValue(rupiahFormatter.format(totalIncome))
                    _totalExpense.postValue(rupiahFormatter.format(totalExpense))
                    _totalProfit.postValue(rupiahFormatter.format(totalProfit))
                } else {
                    _transactions.postValue(emptyList())
                    _totalIncome.postValue("Rp 0")
                    _totalExpense.postValue("Rp 0")
                    _totalProfit.postValue("Rp 0")
                }
            } catch (e: Exception) {
                _errorMessage.postValue("Error: ${e.message}")
            } finally {
                _isLoading.postValue(false)
            }
        }
    }
}