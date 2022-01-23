package com.example.mailapi.ui

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.mailapi.repositories.MailRepository

class MailViewModelProviderFactory(
    val app: Application,
    private val mailRepository: MailRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return MailViewModel(app, mailRepository) as T
    }
}