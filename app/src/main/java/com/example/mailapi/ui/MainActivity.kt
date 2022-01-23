package com.example.mailapi.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.mailapi.databinding.ActivityMainBinding
import com.example.mailapi.repositories.MailRepository


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    lateinit var viewModel: MailViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val mailRepository = MailRepository()
        val viewModelProviderFactory = MailViewModelProviderFactory(application, mailRepository)
        viewModel =
            ViewModelProvider(this, viewModelProviderFactory).get(MailViewModel::class.java)
    }
}