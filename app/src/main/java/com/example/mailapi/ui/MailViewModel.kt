package com.example.mailapi.ui

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.ConnectivityManager.*
import android.net.NetworkCapabilities.*
import android.os.Build
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.mailapi.MailApplication
import com.example.mailapi.models.CreateAccountResponse
import com.example.mailapi.models.DomainResponse
import com.example.mailapi.models.LoginResponse
import com.example.mailapi.models.MessageResponse
import com.example.mailapi.repositories.MailRepository
import com.example.mailapi.util.Resource
import kotlinx.coroutines.launch
import retrofit2.Response
import java.io.IOException

class MailViewModel(
    app: Application,
    private val mailRepository: MailRepository
) : AndroidViewModel(app) {

    val messages: MutableLiveData<Resource<MessageResponse>> = MutableLiveData()
    val domainResponse: MutableLiveData<Resource<DomainResponse>> = MutableLiveData()
    val createAccountResponse: MutableLiveData<Resource<CreateAccountResponse>> = MutableLiveData()
    val loginResponse: MutableLiveData<Resource<LoginResponse>> = MutableLiveData()

    fun getMessages(token: String) = viewModelScope.launch {
        messages.postValue(Resource.Loading())
        try {
            if (hasInternetConnection()) {
                // get message response
                val response = mailRepository.getMessages(token)
                // handle the response
                messages.postValue(handleMessageResponse(response))
            } else {
                messages.postValue(Resource.Error("No internet connection"))
            }
        } catch (t: Throwable) {
            when (t) {
                is IOException -> messages.postValue(Resource.Error("Network failure"))
                else -> messages.postValue(Resource.Error("Conversion error"))
            }
        }
    }

    private fun handleMessageResponse(response: Response<MessageResponse>): Resource<MessageResponse> {
        if (response.isSuccessful) {
            response.body()?.let { resultResponse ->
                return Resource.Success(resultResponse)
            }
        }
        return Resource.Error(response.message())
    }

    fun getDomain() = viewModelScope.launch {
        domainResponse.postValue(Resource.Loading())
        try {
            if (hasInternetConnection()) {
                // get login response
                val response = mailRepository.getDomains()
                // handle the response
                domainResponse.postValue(handleDomainResponse(response))
            } else {
                domainResponse.postValue(Resource.Error("No internet connection"))
            }
        } catch (t: Throwable) {
            when (t) {
                is IOException -> domainResponse.postValue(Resource.Error("Network failure"))
                else -> domainResponse.postValue(Resource.Error("Domain not found"))
            }
        }
    }

    private fun handleDomainResponse(response: Response<DomainResponse>): Resource<DomainResponse> {
        if (response.isSuccessful) {
            response.body()?.let { resultResponse ->
                return Resource.Success(resultResponse)
            }
        } else if (response.message().isNullOrEmpty()) {
            return Resource.Error("Domain not found")
        }
        return Resource.Error(response.message())
    }

    fun login(credential: HashMap<String, String>) = viewModelScope.launch {
        loginResponse.postValue(Resource.Loading())
        try {
            if (hasInternetConnection()) {
                // get login response
                val response = mailRepository.login(credential)
                // handle the response
                loginResponse.postValue(handleLoginResponse(response))
            } else {
                loginResponse.postValue(Resource.Error("No internet connection"))
            }
        } catch (t: Throwable) {
            when (t) {
                is IOException -> loginResponse.postValue(Resource.Error("Network failure"))
                else -> loginResponse.postValue(Resource.Error("Authentication failed"))
            }
        }
    }

    private fun handleLoginResponse(response: Response<LoginResponse>): Resource<LoginResponse> {
        if (response.isSuccessful) {
            response.body()?.let { resultResponse ->
                return Resource.Success(resultResponse)
            }
        } else if (response.message().isNullOrEmpty()) {
            return Resource.Error("Authentication failed")
        }
        return Resource.Error(response.message())
    }

    fun createAccount(credential: HashMap<String, String>) = viewModelScope.launch {
        createAccountResponse.postValue(Resource.Loading())
        try {
            if (hasInternetConnection()) {
                // get createAccount response
                val response = mailRepository.createAccount(credential)
                // handle the response
                createAccountResponse.postValue(handleCreateAccountResponse(response))
            } else {
                createAccountResponse.postValue(Resource.Error("No internet connection"))
            }
        } catch (t: Throwable) {
            when (t) {
                is IOException -> createAccountResponse.postValue(Resource.Error("Network failure"))
                else -> createAccountResponse.postValue(Resource.Error("Invalid email or password"))
            }
        }
    }

    private fun handleCreateAccountResponse(response: Response<CreateAccountResponse>): Resource<CreateAccountResponse> {
        if (response.isSuccessful) {
            response.body()?.let { resultResponse ->
                return Resource.Success(resultResponse)
            }
        } else if (response.message().isNullOrEmpty()) {
            return Resource.Error("Invalid email or password")
        }
        return Resource.Error(response.message())
    }

    suspend fun getCurrentUser(token: String): String? {
        try {
            // get user response
            val response = mailRepository.getCurrentUser(token)
            // handle the response
            if (response.isSuccessful) {
                return response.body()?.address
            }
        } catch (t: Throwable) {
            return "loading..."
        }
        return "loading..."
    }

    // Check internet connection
    private fun hasInternetConnection(): Boolean {
        val connectivityManager = getApplication<MailApplication>().getSystemService(
            Context.CONNECTIVITY_SERVICE
        ) as ConnectivityManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val activeNetwork = connectivityManager.activeNetwork ?: return false
            val capabilities =
                connectivityManager.getNetworkCapabilities(activeNetwork) ?: return false
            return when {
                capabilities.hasTransport(TRANSPORT_WIFI) -> return true
                capabilities.hasTransport(TRANSPORT_CELLULAR) -> return true
                capabilities.hasTransport(TRANSPORT_ETHERNET) -> return true
                else -> false
            }
        } else {
            connectivityManager.activeNetworkInfo?.run {
                return when (type) {
                    TYPE_WIFI -> true
                    TYPE_MOBILE -> true
                    TYPE_ETHERNET -> true
                    else -> false
                }
            }
        }
        return false
    }

}