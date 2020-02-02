package com.hefny.hady.animalfeed.ui.auth

import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import com.hefny.hady.animalfeed.R
import com.hefny.hady.animalfeed.ui.BaseActivity
import com.hefny.hady.animalfeed.viewmodels.ViewModelProviderFactory
import javax.inject.Inject


class AuthActivity : BaseActivity() {

    @Inject
    lateinit var providerFactory: ViewModelProviderFactory

    lateinit var viewModel: AuthViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_auth)

        viewModel = ViewModelProvider(this, providerFactory).get(AuthViewModel::class.java)
    }
}