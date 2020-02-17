package com.hefny.hady.animalfeed.ui.auth

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.hefny.hady.animalfeed.R
import com.hefny.hady.animalfeed.ui.BaseActivity
import com.hefny.hady.animalfeed.ui.ResponseType
import com.hefny.hady.animalfeed.ui.main.MainActivity
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
        subscribeObservers()
    }

    private fun subscribeObservers() {

        viewModel.dataState.observe(this, Observer { dataState ->
            dataState.data?.let { data ->
                data.data?.let { event ->
                    event.getContentIfNotHandled()?.let {
                        it.authToken?.let {
                            Log.d(TAG, "AuthActivity, DataState: $it")
                            viewModel.setAuthToken(it)
                        }
                    }
                }
                data.response?.let { event ->
                    event.getContentIfNotHandled()?.let {
                        when(it.responseType){
                            is ResponseType.Dialog->{
                                // show dialog
                            }
                            is ResponseType.Toast->{
                                // show toats
                            }
                            is ResponseType.None->{
                                // print to log
                                Log.e(TAG, "AuthActivity, Response: ${it.message}, ${it.responseType} ")
                            }
                        }
                    }
                }
            }

        })

        viewModel.viewState.observe(this, Observer
        {
            Log.d(TAG, "AuthActivity, subscribeObservers: AuthViewState: ${it}")
            it.authToken?.let {
                sessionManager.login(it)
            }
        })
        sessionManager.cachedToken.observe(this, Observer
        { dataState ->
            Log.d(TAG, "AuthActivity, subscribeObservers: AuthDataState: ${dataState}")
            dataState.let { authToken ->
                if (authToken != null && authToken.account_pk != -1 && authToken.token != null) {
                    navMainActivity()
                }
            }
        })
    }

    fun navMainActivity() {
        Log.d(TAG, "navMainActivity: called.")
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }
}