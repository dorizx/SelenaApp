package com.example.selenaapp.data.repository

import com.example.selenaapp.data.api.ApiConfig
import com.example.selenaapp.data.preference.UserPreference
import com.example.selenaapp.data.response.DashboardResponse
import com.example.selenaapp.data.response.DataItem
import kotlinx.coroutines.flow.first
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class HomeRepository(private val userPreference: UserPreference) {

    suspend fun getAnomalyTransactions(): DashboardResponse {
        val user = userPreference.getSession().first()
        val token = user.token
        val userId = user.userId

        val response = ApiConfig.getApiService(token).getDashboard(userId)

        if (response.isSuccessful) {
            val body = response.body()!!
            return DashboardResponse(
                anomalyTransactions = body.anomalyTransactions,
                totalIncome = body.totalIncome,
                totalExpense = body.totalExpense,
                financialAdvice = body.financialAdvice,
            )
        } else {
            throw Exception("Failed to fetch data")
        }
    }
}
