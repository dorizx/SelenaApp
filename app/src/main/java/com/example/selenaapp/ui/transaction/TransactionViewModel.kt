package com.example.selenaapp.ui.transaction

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class TransactionViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "Ini Transaksi"
    }
    val text: LiveData<String> = _text
}