package com.example.selenaapp.data.preference

import android.content.ContentValues.TAG
import android.content.Context
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.selenaapp.data.response.AnomalyTransactionsItem
import com.example.selenaapp.data.response.DashboardResponse
import com.example.selenaapp.data.response.Transaction
import com.google.gson.Gson
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "session")

class UserPreference private constructor(private val dataStore: DataStore<Preferences>) {

    suspend fun saveSession(user: UserModel) {
        Log.d(TAG, "saveSession: Saving user session: $user")
        dataStore.edit { preferences ->
            preferences[NAME_KEY] = user.name
            preferences[EMAIL_KEY] = user.email
            preferences[TOKEN_KEY] = user.token
            preferences[IS_LOGIN_KEY] = true
            preferences[USER_ID_KEY] = user.userId
            preferences[IS_DATA_LOADED] = true
            Log.d(TAG, "saveSession: User session saved: ${preferences.toString()}")
        }
    }

    fun getSession(): Flow<UserModel> {
        return dataStore.data.map { preferences ->
            UserModel(
                preferences[NAME_KEY] ?: "",
                preferences[EMAIL_KEY] ?: "",
                preferences[TOKEN_KEY] ?: "",
                preferences[IS_LOGIN_KEY] ?: false,
                preferences[USER_ID_KEY] ?: 0,
                preferences[IS_DATA_LOADED] ?: false
            )
        }
    }

    suspend fun saveData(
        transactions: List<AnomalyTransactionsItem?>,
        financialAdvice: String?,
        totalIncome: Float,
        totalExpense: Float,
        isDataLoaded: Boolean
    ) {
        dataStore.edit { preferences ->
            preferences[TRANSACTIONS_KEY] = Gson().toJson(transactions) // Menyimpan dalam format JSON
            preferences[FINANCIAL_ADVICE_KEY] = financialAdvice.orEmpty()
            preferences[TOTAL_INCOME_KEY] = totalIncome
            preferences[TOTAL_EXPENSE_KEY] = totalExpense
            preferences[IS_DATA_LOADED] = isDataLoaded
        }
    }

    fun getSavedData(): Flow<SavedDashboard> {
        return dataStore.data.map { preferences ->
            SavedDashboard(
                Gson().fromJson(preferences[TRANSACTIONS_KEY], Array<AnomalyTransactionsItem>::class.java).toList(),
                preferences[FINANCIAL_ADVICE_KEY],
                preferences[TOTAL_INCOME_KEY] ?: 0f,
                preferences[TOTAL_EXPENSE_KEY] ?: 0f
            )
        }
    }

    suspend fun logout() {
        dataStore.edit { preferences ->
            preferences.clear()
        }
    }

    companion object {
        @Volatile
        private var INSTANCE: UserPreference? = null
        private val NAME_KEY = stringPreferencesKey("name")
        private val EMAIL_KEY = stringPreferencesKey("email")
        private val TOKEN_KEY = stringPreferencesKey("token")
        private val IS_LOGIN_KEY = booleanPreferencesKey("isLogin")
        private val USER_ID_KEY = intPreferencesKey("userId")
        private val IS_DATA_LOADED = booleanPreferencesKey("isDataLoaded")
        private val TRANSACTIONS_KEY = stringPreferencesKey("transactions")
        private val FINANCIAL_ADVICE_KEY = stringPreferencesKey("financialAdvice")
        private val TOTAL_INCOME_KEY = floatPreferencesKey("totalIncome")
        private val TOTAL_EXPENSE_KEY = floatPreferencesKey("totalExpense")

        fun getInstance(dataStore: DataStore<Preferences>): UserPreference {
            return INSTANCE ?: synchronized(this) {
                val instance = UserPreference(dataStore)
                INSTANCE = instance
                instance
            }
        }
    }
}