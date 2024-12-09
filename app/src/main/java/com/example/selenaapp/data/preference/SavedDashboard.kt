package com.example.selenaapp.data.preference

import com.example.selenaapp.data.response.AnomalyTransactionsItem
import com.example.selenaapp.data.response.Transaction

data class SavedDashboard(
    val transactions: List<AnomalyTransactionsItem> = emptyList(),
    val financialAdvice: String?,
    val totalIncome: Float,
    val totalExpense: Float
)
