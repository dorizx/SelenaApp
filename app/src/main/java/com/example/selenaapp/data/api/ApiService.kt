package com.example.selenaapp.data.api

import com.example.selenaapp.data.response.DashboardResponse
import com.example.selenaapp.data.response.DeleteAllResponse
import com.example.selenaapp.data.response.DeleteResponse
import com.example.selenaapp.data.response.DetailResponse
import com.example.selenaapp.data.response.FormResponse
import com.example.selenaapp.data.response.LoginResponse
import com.example.selenaapp.data.response.OtpResponse
import com.example.selenaapp.data.response.ShopeeResponse
import com.example.selenaapp.data.response.SignupResponse
import com.example.selenaapp.data.response.TokopediaResponse
import com.example.selenaapp.data.response.TransactionResponse
import com.example.selenaapp.data.response.UpdateResponse
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.DELETE
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query

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

    @GET("/transactions")
    suspend fun getTransactions(
        @Query("user_id") userId: Int
    ) : Response<TransactionResponse>

    @DELETE("/transactions/{transactionId}")
    suspend fun deleteTransaction(
        @Path("transactionId") transactionId: Int
    ) : Response<DeleteResponse>

    @GET("/dashboard")
    suspend fun getDashboard(
        @Query("user_id") userId: Int
    ) : Response<DashboardResponse>

    @FormUrlEncoded
    @PUT("/transactions/{transactionId}")
    suspend fun updateTransaction(
        @Path("transactionId") transactionId: Int,
        @Field("amount") amount: Int,
        @Field("transaction_type") transactionType: String,
        @Field("date") date: String,
        @Field("catatan") note: String
    ) : Response<UpdateResponse>

    @GET("/transactions/{transactionId}")
    suspend fun getDetailTransaction(
        @Path("transactionId") transactionId: Int
    ) : Response<DetailResponse>

    @DELETE("/transactions/delete-all/{userId}")
    suspend fun deleteAllTransaction(
        @Path("userId") userId: Int
    ) : Response<DeleteAllResponse>
}

