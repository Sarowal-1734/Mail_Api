package com.example.mailapi.ui.fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.mailapi.R
import com.example.mailapi.databinding.FragmentLoginBinding
import com.example.mailapi.repositories.MailRepository
import com.example.mailapi.ui.MailViewModel
import com.example.mailapi.ui.MailViewModelProviderFactory
import com.example.mailapi.util.Constants.Companion.JWT_TOKEN
import com.example.mailapi.util.Constants.Companion.SHARED_PREF
import com.example.mailapi.util.Resource

class LoginFragment : Fragment() {

    private lateinit var binding: FragmentLoginBinding
    private lateinit var viewModel: MailViewModel

    // on back pressed to exit the app
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activity?.onBackPressedDispatcher?.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                activity?.finish()
            }
        })
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentLoginBinding.inflate(layoutInflater, container, false)

        val mailRepository = MailRepository()
        val viewModelProviderFactory =
            MailViewModelProviderFactory(activity?.application!!, mailRepository)
        viewModel = ViewModelProvider(this, viewModelProviderFactory).get(MailViewModel::class.java)

        // on login button clicked
        binding.buttonLogin.setOnClickListener {
            closeKeyboard()
            val email = binding.editTextTextMailAddress.text.toString()
            val password = binding.editTextTextPassword.text.toString()
            val credential: HashMap<String, String> = HashMap()
            credential["address"] = email
            credential["password"] = password
            viewModel.login(credential)
        }

        // state observer
        viewModel.loginResponse.observe(viewLifecycleOwner, { response ->
            when (response) {
                is Resource.Success -> {
                    hideProgressBar()
                    response.data?.let { resultResponse ->
                        saveToken(resultResponse.token)
                    }
                }
                is Resource.Error -> {
                    hideProgressBar()
                    response.message?.let { message ->
                        Toast.makeText(activity, message, Toast.LENGTH_SHORT).show()
                    }
                }
                is Resource.Loading -> {
                    showMainProgressBar()
                }
            }
        })

        // on create account button clicked
        binding.buttonCreateAccount.setOnClickListener {
            this.findNavController().navigate(R.id.createAccountFragment)
        }

        return binding.root
    }

    // hide progressBar
    private fun hideProgressBar() {
        binding.progressBar.isVisible = false
    }

    // show progressBar
    private fun showMainProgressBar() {
        binding.progressBar.isVisible = true
    }

    // Save JWT token for stay logged in until logout
    private fun saveToken(token: String) {
        val pref = activity?.getSharedPreferences(SHARED_PREF, Context.MODE_PRIVATE)
        val editor = pref?.edit()
        editor?.apply {
            putString(JWT_TOKEN, token)
        }?.apply()
        Toast.makeText(context, "Login Success", Toast.LENGTH_SHORT).show()
        this@LoginFragment.findNavController().navigate(R.id.homeFragment)
    }

    // Hide keyboard
    private fun closeKeyboard() {
        val manager =
            view?.context?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        manager.hideSoftInputFromWindow(view?.windowToken, 0)
    }

}