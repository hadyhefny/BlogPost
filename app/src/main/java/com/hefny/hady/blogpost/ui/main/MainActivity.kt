package com.hefny.hady.blogpost.ui.main

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.NavController
import com.bumptech.glide.RequestManager
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.hefny.hady.blogpost.R
import com.hefny.hady.blogpost.models.AUTH_TOKEN_BUNDLE_KEY
import com.hefny.hady.blogpost.models.AuthToken
import com.hefny.hady.blogpost.ui.BaseActivity
import com.hefny.hady.blogpost.ui.auth.AuthActivity
import com.hefny.hady.blogpost.ui.main.account.BaseAccountFragment
import com.hefny.hady.blogpost.ui.main.account.ChangePasswordFragment
import com.hefny.hady.blogpost.ui.main.account.UpdateAccountFragment
import com.hefny.hady.blogpost.ui.main.blog.BaseBlogFragment
import com.hefny.hady.blogpost.ui.main.blog.UpdateBlogFragment
import com.hefny.hady.blogpost.ui.main.blog.ViewBlogFragment
import com.hefny.hady.blogpost.ui.main.create_blog.BaseCreateBlogFragment
import com.hefny.hady.blogpost.util.BOTTOM_NAVIGATION_BACKSTACK_BUNDLE_KEY
import com.hefny.hady.blogpost.util.BottomNavController
import com.hefny.hady.blogpost.util.BottomNavController.*
import com.hefny.hady.blogpost.util.setUpNavigation
import com.hefny.hady.blogpost.viewmodels.ViewModelProviderFactory
import kotlinx.android.synthetic.main.activity_main.*
import javax.inject.Inject

class MainActivity : BaseActivity(),
    NavGraphProvider,
    OnNavigationGraphChanged,
    OnNavigationReselectedListener,
    MainDependencyProvider {

    @Inject
    lateinit var requestManager: RequestManager

    @Inject
    lateinit var providerFactory: ViewModelProviderFactory

    private lateinit var bottomNavigationView: BottomNavigationView
    private val bottomNavController by lazy(LazyThreadSafetyMode.NONE) {
        BottomNavController(
            this,
            R.id.main_nav_host_fragment,
            R.id.nav_blog,
            this,
            this
        )
    }

    override fun getViewModelProviderFactory() = providerFactory
    override fun getGlideRequestManager() = requestManager
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setupActionBar()
        setupBottomNavView(savedInstanceState)
        subscribeObservers()
        restoreSession(savedInstanceState)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putParcelable(AUTH_TOKEN_BUNDLE_KEY, sessionManager.cachedToken.value)
        outState.putIntArray(
            BOTTOM_NAVIGATION_BACKSTACK_BUNDLE_KEY,
            bottomNavController.navigationBackStack.toIntArray()
        )
        super.onSaveInstanceState(outState)
    }

    private fun setupBottomNavView(savedInstanceState: Bundle?) {
        bottomNavigationView = findViewById(R.id.bottom_navigation_view)
        bottomNavigationView.setUpNavigation(bottomNavController, this)
        if (savedInstanceState == null) {
            bottomNavController.setupBottomNavBackStack(null)
            bottomNavController.onNavigationItemSelected()
        } else {
            (savedInstanceState[BOTTOM_NAVIGATION_BACKSTACK_BUNDLE_KEY] as IntArray?)?.let { items ->
                val backStack = BackStack()
                backStack.addAll(items.toTypedArray())
                bottomNavController.setupBottomNavBackStack(backStack)
            }
        }
    }

    private fun restoreSession(savedInstanceState: Bundle?) {
        savedInstanceState?.let { inState ->
            inState[AUTH_TOKEN_BUNDLE_KEY]?.let { authToken ->
                sessionManager.setValue(authToken as AuthToken)
            }
        }
    }

    private fun setupActionBar() {
        setSupportActionBar(tool_bar)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> onBackPressed()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() = bottomNavController.onBackPressed()

    fun subscribeObservers() {
        sessionManager.cachedToken.observe(this, Observer { authToken ->
            Log.d(TAG, "MainActivity, subscribeObservers: ViewState: ${authToken}")
            if (authToken == null || authToken.account_pk == -1 || authToken.token == null) {
                navAuthActivity()
            }
        })
    }

    private fun navAuthActivity() {
        val intent = Intent(this, AuthActivity::class.java)
        startActivity(intent)
        finish()
    }

    override fun displayProgressBar(loading: Boolean) {
        if (loading) {
            progress_bar.visibility = View.VISIBLE
        } else {
            progress_bar.visibility = View.GONE
        }
    }

    override fun getNavGraphId(itemId: Int) = when (itemId) {
        R.id.nav_blog -> {
            R.navigation.nav_blog
        }
        R.id.nav_account -> {
            R.navigation.nav_account
        }
        R.id.nav_create_blog -> {
            R.navigation.nav_create_blog
        }
        else -> R.navigation.nav_blog
    }

    override fun onGraphChange() {
        expandAppBar(R.id.app_bar)
        cancelActiveJobs()
    }

    private fun cancelActiveJobs() {
        val fragments = bottomNavController.fragmentManager
            .findFragmentById(bottomNavController.containerId)
            ?.childFragmentManager
            ?.fragments
        if (fragments != null) {
            for (fragment in fragments) {
                when (fragment) {
                    is BaseAccountFragment -> fragment.cancelActiveJobs()
                    is BaseBlogFragment -> fragment.cancelActiveJobs()
                    is BaseCreateBlogFragment -> fragment.cancelActiveJobs()
                }
            }
        }
        displayProgressBar(false)
    }

    override fun onReselectNavItem(navController: NavController, fragment: Fragment) =
        when (fragment) {
            is ViewBlogFragment -> {
                navController.navigate(R.id.action_viewBlogFragment_to_blogFragment)
            }
            is UpdateBlogFragment -> {
                navController.navigate(R.id.action_updateBlogFragment_to_blogFragment)
            }
            is UpdateAccountFragment -> {
                navController.navigate(R.id.action_updateAccountFragment_to_accountFragment)
            }
            is ChangePasswordFragment -> {
                navController.navigate(R.id.action_changePasswordFragment_to_accountFragment)
            }
            else -> {
                // do nothing
            }
        }
}