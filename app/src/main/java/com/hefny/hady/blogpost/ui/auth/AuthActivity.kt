package com.hefny.hady.blogpost.ui.auth

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.viewModels
import androidx.fragment.app.FragmentFactory
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.hefny.hady.blogpost.BaseApplication
import com.hefny.hady.blogpost.R
import com.hefny.hady.blogpost.fragments.auth.AuthNavHostFragment
import com.hefny.hady.blogpost.ui.BaseActivity
import com.hefny.hady.blogpost.ui.auth.state.AuthStateEvent
import com.hefny.hady.blogpost.ui.main.MainActivity
import kotlinx.android.synthetic.main.activity_auth.*
import javax.inject.Inject


class AuthActivity : BaseActivity() {

    @Inject
    lateinit var fragmentFactory: FragmentFactory

    @Inject
    lateinit var providerFactory: ViewModelProvider.Factory

    val viewModel: AuthViewModel by viewModels {
        providerFactory
    }

    override fun inject() {
        (application as BaseApplication).authComponent().inject(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_auth)
        subscribeObservers()
        onRestoreInstanceState()
    }

    private fun onRestoreInstanceState() {
        val host = supportFragmentManager
            .findFragmentById(R.id.auth_fragments_container)
        host?.let {
            // do nothing
        } ?: createNavHost()
    }

    private fun createNavHost() {
        val navHost = AuthNavHostFragment.create(
            R.navigation.auth_nav_graph
        )
        supportFragmentManager.beginTransaction()
            .replace(
                R.id.auth_fragments_container,
                navHost,
                getString(R.string.AuthNavHost)
            )
            .setPrimaryNavigationFragment(navHost)
            .commit()
    }

    override fun onResume() {
        super.onResume()
        checkPreviousAuthUser()
    }

    private fun checkPreviousAuthUser() {
        viewModel.setStateEvent(AuthStateEvent.CheckPreviousAuthEvent())
    }

    private fun subscribeObservers() {
        viewModel.dataState.observe(this, Observer { dataState ->
            if (dataState != null) {
                onDataStateChange(dataState)
                dataState.data?.let { data ->
                    data.data?.let { event ->
                        event.getContentIfNotHandled()?.let {
                            it.authToken?.let {
                                Log.d(TAG, "AuthActivity, DataState: $it")
                                viewModel.setAuthToken(it)
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
        (application as BaseApplication).releaseAuthComponent()
    }

    override fun displayProgressBar(loading: Boolean) {
        if (loading) {
            progress_bar.visibility = View.VISIBLE
        } else {
            progress_bar.visibility = View.GONE
        }
    }
}