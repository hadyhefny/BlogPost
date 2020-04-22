package com.hefny.hady.blogpost.ui.auth


import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.hefny.hady.blogpost.R
import com.hefny.hady.blogpost.di.auth.AuthScope
import kotlinx.android.synthetic.main.fragment_launcher.*
import javax.inject.Inject

@AuthScope
class LauncherFragment
@Inject
constructor(
    private val viewModelFactory: ViewModelProvider.Factory
) : Fragment(R.layout.fragment_launcher) {

    val viewModel: AuthViewModel by viewModels {
        viewModelFactory
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        viewModel.cancelActiveJobs()
        super.onCreate(savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        btn_login.setOnClickListener {
            navLogin()
        }
        btn_register.setOnClickListener {
            navRegister()
        }
        btn_forgotPassword.setOnClickListener {
            navForgotPassword()
        }

        Log.d("LauncherFragment", "onViewCreated: ${viewModel.hashCode()}")
    }

    private fun navLogin() {
        findNavController().navigate(R.id.action_launcherFragment_to_loginFragment)
    }

    private fun navRegister() {
        findNavController().navigate(R.id.action_launcherFragment_to_registerFragment)
    }

    private fun navForgotPassword() {
        findNavController().navigate(R.id.action_launcherFragment_to_forgotPasswordFragment)
    }
}