package com.example.mailapi.repositories

import com.example.mailapi.api.RetrofitInstance
import com.example.mailapi.util.Constants.Companion.QUERY_PAGE_SIZE

class MailRepository {

    suspend fun getDomains() = RetrofitInstance.api.getDomains(QUERY_PAGE_SIZE)

    suspend fun createAccount(credential: HashMap<String, String>) =
        RetrofitInstance.api.createAccount(credential)

    suspend fun login(credential: HashMap<String, String>) = RetrofitInstance.api.login(credential)

    suspend fun getMessages(token: String) =
        RetrofitInstance.api.getMessages(QUERY_PAGE_SIZE, token)

    suspend fun getCurrentUser(token: String) =
        RetrofitInstance.api.getCurrentUser(token)
}