package com.example.selenaapp.data.preference

data class UserModel (
    val name: String,
    val email: String,
    val token: String,
    val isLogin: Boolean = false,
    val userId: Int,
    val isDataLoaded: Boolean = false
)