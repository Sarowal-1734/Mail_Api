package com.example.mailapi.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.mailapi.R
import com.example.mailapi.databinding.FragmentCreateAccountBinding
import com.example.mailapi.ui.MailViewModel
import com.example.mailapi.ui.MainActivity
import com.example.mailapi.util.Resource

class CreateAccountFragment : Fragment() {

    private lateinit var binding: FragmentCreateAccountBinding
    private lateinit var viewModel: MailViewModel
    private var domain: String = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentCreateAccountBinding.inflate(layoutInflater, container, false)

        viewModel = (activity as MainActivity).viewModel

        viewModel.getDomain()

        // states observer
        viewModel.domainResponse.observe(viewLifecycleOwner, { response ->
            when (response) {
                is Resource.Success -> {
                    hideProgressBar()
                    response.data?.let { domainResponse ->
                        domain = domainResponse.hydra_member[0].domain
                        binding.textViewDomain.text = "@$domain"
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

        binding.buttonSignUp.setOnClickListener {
            val email = binding.editTextTextMailAddress.text.toString()
            val password = binding.editTextTextPassword.text.toString()
            val address = "$email@$domain"
            val credential: HashMap<String, String> = HashMap()
            credential["address"] = address
            credential["password"] = password
            viewModel.createAccount(credential)
        }

        // states observer
        viewModel.createAccountResponse.observe(viewLifecycleOwner, { response ->
            when (response) {
                is Resource.Success -> {
                    hideProgressBar()
                    response.data?.let {
                        Toast.makeText(context, "Account Created", Toast.LENGTH_SHORT).show()
                        this@CreateAccountFragment.findNavController().navigate(R.id.loginFragment)
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
}