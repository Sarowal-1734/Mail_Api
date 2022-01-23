package com.example.mailapi.api

import com.example.mailapi.models.*
import retrofit2.Response
import retrofit2.http.*

interface Api {
    @GET("/domains")
    suspend fun getDomains(
        @Query("page")
        page: Int
    ): Response<DomainResponse>

    @POST("/accounts")
    suspend fun createAccount(
        @Body
        credential: HashMap<String, String>
    ): Response<CreateAccountResponse>

    @POST("/token")
    suspend fun login(
        @Body
        credential: HashMap<String, String>
    ): Response<LoginResponse>

    @GET("/messages")
    suspend fun getMessages(
        @Query("page")
        page: Int,
        @Header("Authorization")
        token: String
    ): Response<MessageResponse>

    @GET("/me")
    suspend fun getCurrentUser(
        @Header("Authorization")
        token: String
    ): Response<CurrentUserResponse>

}