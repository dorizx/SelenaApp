package com.example.selenaapp.data.api

import com.example.selenaapp.data.response.FormResponse
import com.example.selenaapp.data.response.LoginResponse
import com.example.selenaapp.data.response.OtpResponse
import com.example.selenaapp.data.response.ShopeeResponse
import com.example.selenaapp.data.response.SignupResponse
import com.example.selenaapp.data.response.TokopediaResponse
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

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
    @POST("/transactions")
    suspend fun addTransaction(
        //@Path("transactionsId") transactionsId: String,
        @Field("user_id") userId: String,
        @Field("amount") amount: Int,
        @Field("transaction_type") type: String,
        @Field("date") date: String,
        @Field("catatan") note: String
    ) : Response<FormResponse>

    @Multipart
    @POST("/insert-shopee")
    suspend fun addShopeeTransaction(
        @Part("user_id") userId: Int,
        @Part file: MultipartBody.Part,
    ) : Response<ShopeeResponse>

    @Multipart
    @POST("/insert-tokopedia")
    suspend fun addTokopediaTransaction(
        @Part("user_id") userId: Int,
        @Part file: MultipartBody.Part,
    ) : Response<TokopediaResponse>
}

