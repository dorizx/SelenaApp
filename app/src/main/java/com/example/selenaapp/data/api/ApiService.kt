package com.example.selenaapp.data.api

import com.example.selenaapp.data.response.LoginResponse
import com.example.selenaapp.data.response.OtpResponse
import com.example.selenaapp.data.response.SignupResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.Headers
import retrofit2.http.POST

interface ApiService {

    @FormUrlEncoded
    @POST("/auth/signup")
    //suspend fun signup(@Body signupRequest: SignupRequest): Response<SignupResponse>
    suspend fun signup(
        @Field("name") name: String,
        @Field("email") email: String,
        @Field("password") password: String
    ): Response<SignupResponse>

    @FormUrlEncoded
    @POST("login")
    suspend fun login(
        @Field("email") email: String,
        @Field("password") password: String
    ): Response<LoginResponse>

    @FormUrlEncoded
    @POST("/auth/otp/verify")
    //suspend fun verifyOtp(@Body otpRequest: OtpRequest): Response<OtpResponse>
    suspend fun verifyOtp(
        @Field("otp") otp: String,
        @Field("name") name: String,
        @Field("email") email: String,
        @Field("password") password: String
    ) : Response<OtpResponse>
}

