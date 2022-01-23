package com.example.mailapi.ui.fragments

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mailapi.R
import com.example.mailapi.adapters.MailAdapter
import com.example.mailapi.databinding.FragmentHomeBinding
import com.example.mailapi.ui.MailViewModel
import com.example.mailapi.ui.MainActivity
import com.example.mailapi.util.Constants.Companion.JWT_TOKEN
import com.example.mailapi.util.Constants.Companion.SHARED_PREF
import com.example.mailapi.util.Resource

class HomeFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding
    private lateinit var viewModel: MailViewModel
    private lateinit var mailAdapter: MailAdapter

    private lateinit var sharedPref: SharedPreferences
    private var currentUser: String = "loading"

    // on back pressed to exit the app
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activity?.onBackPressedDispatcher?.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                activity?.finish()
            }
        })
    }

    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentHomeBinding.inflate(layoutInflater, container, false)

        viewModel = (activity as MainActivity).viewModel

        // check current user is null or not
        // if not null then goto inbox fragment (home fragment)
        sharedPref = activity?.getSharedPreferences(SHARED_PREF, Context.MODE_PRIVATE)!!
        val token = sharedPref.getString(JWT_TOKEN, null)
        if (token == null) {
            this.findNavController().navigate(R.id.loginFragment)
        }

        // Setup recycler adapter
        setupRecyclerView()

        // get messages or mails
        viewModel.getMessages("Bearer $token")

        // get current user's mail address
        getCurrentUser("Bearer $token")


        // states observer
        viewModel.messages.observe(viewLifecycleOwner, { response ->
            when (response) {
                is Resource.Success -> {
                    hideProgressBar()
                    response.data?.let { messageResponse ->
                        mailAdapter.differ.submitList(messageResponse.hydra_member.toList())
                        // set current user's mail address
                        binding.textViewCurrentUser.text = "Logged in: $currentUser"
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

        // on signOut button clicked
        binding.buttonLogout.setOnClickListener {
            signOut()
        }

        // on recycler item clicked
        mailAdapter.setOnItemClickListener {
            Toast.makeText(context, it.subject, Toast.LENGTH_SHORT).show()
        }

        return binding.root
    }

    // get current user's mail address
    private fun getCurrentUser(token: String) {
        lifecycleScope.launchWhenCreated {
            currentUser = viewModel.getCurrentUser(token).toString()
        }
    }

    // sign out
    private fun signOut() {
        val editor = sharedPref.edit()
        editor?.apply {
            putString(JWT_TOKEN, null)
        }?.apply()
        this@HomeFragment.findNavController().navigate(R.id.loginFragment)
    }

    // hide progressBar
    private fun hideProgressBar() {
        binding.progressBar.isVisible = false
    }

    // show progressBar
    private fun showMainProgressBar() {
        binding.progressBar.isVisible = true
    }

    // Setup recyclerView
    private fun setupRecyclerView() {
        binding.recyclerViewMessages.apply {
            mailAdapter = MailAdapter()
            adapter = mailAdapter
            layoutManager = LinearLayoutManager(activity?.applicationContext)
            addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
        }
    }

}