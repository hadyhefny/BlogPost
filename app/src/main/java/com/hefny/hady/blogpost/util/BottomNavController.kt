package com.hefny.hady.blogpost.util

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.os.Parcelable
import androidx.annotation.IdRes
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.hefny.hady.blogpost.R
import com.hefny.hady.blogpost.fragments.main.account.AccountNavHostFragment
import com.hefny.hady.blogpost.fragments.main.blog.BlogNavHostFragment
import com.hefny.hady.blogpost.fragments.main.create_blog.CreateBlogNavHostFragment
import kotlinx.android.parcel.Parcelize

/**
 * programmatically add nav host fragments which will have their own backstaks
 * depending on which navigation graph has been added to it.
 */

const val BOTTOM_NAVIGATION_BACKSTACK_BUNDLE_KEY =
    "com.hefny.hady.blogpost.util.BottomNavController.BackStack"

class BottomNavController(
    val context: Context,
    @IdRes val containerId: Int,
    @IdRes val appStartDestinationId: Int,
    val graphChangeListener: OnNavigationGraphChanged?
) {
    private val TAG = "AppDebug"
    lateinit var activity: Activity
    lateinit var fragmentManager: FragmentManager
    lateinit var navItemChangeListener: OnNavigationItemChanged
    lateinit var navigationBackStack: BackStack

    init {
        if (context is Activity) {
            activity = context
            fragmentManager = (activity as FragmentActivity).supportFragmentManager
        }
    }

    fun setupBottomNavBackStack(previousBackStack: BackStack?) {
        navigationBackStack = previousBackStack?.let {
            it
        } ?: BackStack.of(appStartDestinationId)
    }

    fun onNavigationItemSelected(menuItemId: Int = navigationBackStack.last()): Boolean {
        // replace fragment representing navigation item
        val fragment = fragmentManager.findFragmentByTag(menuItemId.toString())
            ?: createNavHost(menuItemId)
        fragmentManager.beginTransaction()
            .setCustomAnimations(
                R.anim.fade_in,
                R.anim.fade_out,
                R.anim.fade_in,
                R.anim.fade_out
            )
            .replace(containerId, fragment, menuItemId.toString())
            .addToBackStack(null)
            .commit()
        // add to backstack
        navigationBackStack.moveLast(menuItemId)
        // update checked icon
        navItemChangeListener.onItemChanged(menuItemId)
        // communicate with activity
        // ex: to cancel any pending transactions
        // if the user navigates to another fragment or activity while doing some operations (like api request)
        // so we can notify the repository and cancel that transaction
        graphChangeListener?.onGraphChange()
        // to notify the interface that this method has benn handled -> return true
        return true
    }

    private fun createNavHost(itemId: Int) =
        when (itemId) {
            R.id.menu_nav_account -> {
                AccountNavHostFragment.create(R.navigation.nav_account)
            }
            R.id.menu_nav_blog -> {
                BlogNavHostFragment.create(R.navigation.nav_blog)
            }
            R.id.menu_nav_create_blog -> {
                CreateBlogNavHostFragment.create(R.navigation.nav_create_blog)
            }
            else -> {
                BlogNavHostFragment.create(R.navigation.nav_blog)
            }
        }

    @SuppressLint("RestrictedApi")
    fun onBackPressed() {
//        val childFragmentManager = fragmentManager.findFragmentById(containerId)!!
//            .childFragmentManager
        val navController = fragmentManager.findFragmentById(containerId)!!.findNavController()

        when {
            navController.backStack.size > 2 -> {
                navController.popBackStack()
            }
            // fragment backstack is empty so try to back on the navigation stack
            navigationBackStack.size > 1 -> {
                // remove last item from backstack
                navigationBackStack.removeLast()
                // update container with new fragment
                onNavigationItemSelected()
            }
            // if the stack has only one and it's not the navigation home we should
            // ensure the application always leave from startDestination
            navigationBackStack.last() != appStartDestinationId -> {
                navigationBackStack.removeLast()
                navigationBackStack.add(0, appStartDestinationId)
                onNavigationItemSelected()
            }
            else -> activity.finish()
        }
    }

    @Parcelize
    class BackStack : ArrayList<Int>(), Parcelable {
        companion object {
            fun of(vararg elements: Int): BackStack {
                val b = BackStack()
                b.addAll(elements.toTypedArray())
                return b
            }
        }

        fun removeLast() = removeAt(size - 1)

        // removes the item and add it to the end of the list
        // ex: [1,2,3,4]
        // moveLast(3)
        // result: [1,2,4,3]
        // ex: [1]
        // moveLast(2)
        // result: [1,2]
        fun moveLast(item: Int) {
            remove(item)
            add(item)
        }
    }

    // for setting the checked icon in the bottom nav
    // internal interface: will be implemented in this class only
    interface OnNavigationItemChanged {
        fun onItemChanged(itemId: Int)
    }

    fun setOnItemNavigationChanged(listener: (itemId: Int) -> Unit) {
        this.navItemChangeListener = object : OnNavigationItemChanged {
            override fun onItemChanged(itemId: Int) {
                listener.invoke(itemId)
            }
        }
    }

    // execute when navigation graph changes
    // ex: select a new item on the bottom nav
    // ex: home -> account
    interface OnNavigationGraphChanged {
        fun onGraphChange()
    }

    interface OnNavigationReselectedListener {
        fun onReselectNavItem(navController: NavController, fragment: Fragment)
    }
}

/**
 * kotlin extension function
 * for setting up this class and associating it with all the required listeners to the bottom navigation
 **/
fun BottomNavigationView.setUpNavigation(
    bottomNavController: BottomNavController,
    onReselectListener: BottomNavController.OnNavigationReselectedListener
) {
    setOnNavigationItemSelectedListener {
        bottomNavController.onNavigationItemSelected(it.itemId)
    }
    setOnNavigationItemReselectedListener {
        bottomNavController
            .fragmentManager
            .findFragmentById(bottomNavController.containerId)!!
            .childFragmentManager
            .fragments[0]?.let { fragment ->
            onReselectListener.onReselectNavItem(
                bottomNavController.activity.findNavController(bottomNavController.containerId),
                fragment
            )
        }
    }
    bottomNavController.setOnItemNavigationChanged { itemId ->
        menu.findItem(itemId).isChecked = true
    }
}