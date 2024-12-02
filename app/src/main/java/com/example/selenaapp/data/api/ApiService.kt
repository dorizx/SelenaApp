package com.example.selenaapp.data.api

import com.example.selenaapp.data.response.LoginResponse
import com.example.selenaapp.data.response.OtpResponse
import com.example.selenaapp.data.response.SignupResponse
import retrofit2.Response
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface ApiService {

    @FormUrlEncoded
    @POST("signup")
    suspend fun signup(
        @Field("name") name: String,
        @Field("email") email: String,
        @Field("password") password: String
    ): SignupResponse

    @FormUrlEncoded
    @POST("login")
    suspend fun login(
        @Field("email") email: String,
        @Field("password") password: String
    ): LoginResponse

    @FormUrlEncoded
    @POST("auth/otp/verify")
    suspend fun getOtp(
        @Field("email") email: String,
    ) : OtpResponse
}

