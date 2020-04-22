package com.hefny.hady.blogpost.fragments.auth

import androidx.fragment.app.FragmentFactory
import androidx.lifecycle.ViewModelProvider
import com.hefny.hady.blogpost.di.auth.AuthScope
import com.hefny.hady.blogpost.ui.auth.ForgotPasswordFragment
import com.hefny.hady.blogpost.ui.auth.LauncherFragment
import com.hefny.hady.blogpost.ui.auth.LoginFragment
import com.hefny.hady.blogpost.ui.auth.RegisterFragment
import javax.inject.Inject

@AuthScope
class AuthFragmentFactory
@Inject
constructor(private val viewModelFactory: ViewModelProvider.Factory) : FragmentFactory() {
    override fun instantiate(classLoader: ClassLoader, className: String) =
        when (className) {
            LauncherFragment::class.java.name -> {
                LauncherFragment(viewModelFactory)
            }
            LoginFragment::class.java.name -> {
                LoginFragment(viewModelFactory)
            }
            RegisterFragment::class.java.name -> {
                RegisterFragment(viewModelFactory)
            }
            ForgotPasswordFragment::class.java.name -> {
                ForgotPasswordFragment(viewModelFactory)
            }
            else -> {
                LauncherFragment(viewModelFactory)
            }
        }
}