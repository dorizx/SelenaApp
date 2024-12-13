package com.example.selenaapp.data.preference

import com.example.selenaapp.data.response.AnomalyTransactionsItem

data class SavedDashboard(
    val transactions: List<AnomalyTransactionsItem> = emptyList(),
    val financialAdvice: String?,
    val totalIncome: Float,
    val totalExpense: Float
)
