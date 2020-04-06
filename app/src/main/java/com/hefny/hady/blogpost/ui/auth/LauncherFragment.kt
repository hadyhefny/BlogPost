package com.hefny.hady.blogpost.ui.auth


import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.hefny.hady.blogpost.R
import kotlinx.android.synthetic.main.fragment_launcher.*

class LauncherFragment : BaseAuthFragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_launcher, container, false)
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