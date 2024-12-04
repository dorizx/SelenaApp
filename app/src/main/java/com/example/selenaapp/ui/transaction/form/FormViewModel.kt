package com.example.selenaapp.ui.transaction.form

import android.content.ContentValues.TAG
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import com.bumptech.glide.load.engine.Resource
import com.example.selenaapp.data.api.ApiConfig
import com.example.selenaapp.data.preference.UserPreference
import com.example.selenaapp.data.repository.UserRepository
import com.example.selenaapp.data.response.FormResponse
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.HttpException
import java.io.File

class FormViewModel (private val userPreference: UserPreference) : ViewModel() {

    private val _uploadResult = MutableLiveData<Result<String>>()
    val uploadResult: LiveData<Result<String>> get() = _uploadResult
    val userSession = userPreference.getSession()

    fun addTransaction(
        userId: String,
        amount: Int,
        transactionType: String,
        date: String,
        note: String?
    ) {
        viewModelScope.launch {
            try {
                // Ambil token dari session
                val session = userPreference.getSession().first()
                val apiService = ApiConfig.getApiService(session.token)
                Log.d(TAG, "addTransaction: $apiService")// Pastikan `ApiConfig` sesuai

                // Lakukan POST request
                val response = apiService.addTransaction(
                    userId = userId,
                    amount = amount,
                    type = transactionType,
                    date = date,
                    note = note?: ""
                )

                if (response.isSuccessful) {
                    val message = response.body()?.message ?: "Transaksi berhasil"
                    _uploadResult.postValue(Result.success(message))
                } else {
                    val errorBody = response.errorBody()?.string()
                    val errorResponse = Gson().fromJson(errorBody, FormResponse::class.java)
                    _uploadResult.postValue(Result.failure(Exception(errorResponse.message)))
                }
            } catch (e: Exception) {
                _uploadResult.postValue(Result.failure(e))
            }
        }
    }
}