package com.hefny.hady.blogpost.ui.main

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.NavController
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.hefny.hady.blogpost.R
import com.hefny.hady.blogpost.ui.BaseActivity
import com.hefny.hady.blogpost.ui.auth.AuthActivity
import com.hefny.hady.blogpost.ui.main.account.ChangePasswordFragment
import com.hefny.hady.blogpost.ui.main.account.UpdateAccountFragment
import com.hefny.hady.blogpost.ui.main.blog.UpdateBlogFragment
import com.hefny.hady.blogpost.ui.main.blog.ViewBlogFragment
import com.hefny.hady.blogpost.util.BottomNavController
import com.hefny.hady.blogpost.util.BottomNavController.*
import com.hefny.hady.blogpost.util.setUpNavigation
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : BaseActivity(),
    NavGraphProvider,
    OnNavigationGraphChanged,
    OnNavigationReselectedListener {

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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setupActionBar()
        bottomNavigationView = findViewById(R.id.bottom_navigation_view)
        bottomNavigationView.setUpNavigation(bottomNavController, this)
        if (savedInstanceState == null) {
            bottomNavController.onNavigationItemSelected()
        }
        subscribeObservers()
    }

    private fun setupActionBar() {
        setSupportActionBar(tool_bar)
    }

    private fun expandAppBar() {
        findViewById<AppBarLayout>(R.id.app_bar).setExpanded(true)
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
        expandAppBar()
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