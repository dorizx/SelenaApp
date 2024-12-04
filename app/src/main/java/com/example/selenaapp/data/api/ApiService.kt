package com.example.selenaapp.data.api

import com.example.selenaapp.data.response.FormResponse
import com.example.selenaapp.data.response.LoginResponse
import com.example.selenaapp.data.response.OtpResponse
import com.example.selenaapp.data.response.SignupResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.Path

interface ApiService {

    @FormUrlEncoded
    @POST("/auth/signup")
    suspend fun signup(
        @Field("name") name: String,
        @Field("email") email: String,
        @Field("password") password: String
    ): Response<SignupResponse>

    @FormUrlEncoded
    @POST("/auth/login")
    suspend fun login(
        @Field("email") email: String,
        @Field("password") password: String
    ): LoginResponse

    @FormUrlEncoded
    @POST("/auth/otp/verify")
    suspend fun verifyOtp(
        @Field("otp") otp: String,
        @Field("name") name: String,
        @Field("email") email: String,
        @Field("password") password: String
    ) : Response<OtpResponse>

    @FormUrlEncoded
    @POST("/transactions/{transactionsId}")
    suspend fun addTransaction(
        @Path("transactionsId") transactionsId: String,
        @Field("user_id") userId: String,
        @Field("amount") amount: Int,
        @Field("transaction_type") type: String,
        @Field("date") date: String
    ) : Response<FormResponse>
}

