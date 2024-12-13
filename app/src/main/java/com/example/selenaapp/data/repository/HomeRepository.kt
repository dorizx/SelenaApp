package com.example.selenaapp.data.repository

import com.example.selenaapp.data.api.ApiConfig
import com.example.selenaapp.data.preference.UserPreference
import com.example.selenaapp.data.response.DashboardResponse
import kotlinx.coroutines.flow.first

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
                financialAdvice = body.financialAdvice,
                message = body.message,
                transactionStats = body.transactionStats
            )
        } else {
            throw Exception("Failed to fetch data")
        }
    }
}
