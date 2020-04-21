package com.hefny.hady.blogpost.ui.main.account

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import com.hefny.hady.blogpost.R
import com.hefny.hady.blogpost.di.Injectable
import com.hefny.hady.blogpost.ui.DataStateChangeListener
import com.hefny.hady.blogpost.ui.KeyboardManagement
import com.hefny.hady.blogpost.viewmodels.ViewModelProviderFactory
import javax.inject.Inject

abstract class BaseAccountFragment : Fragment(), Injectable {
    val TAG: String = "AppDebug"

    @Inject
    lateinit var providerFactory: ViewModelProviderFactory

    lateinit var viewModel: AccountViewModel
    lateinit var stateChangeListener: DataStateChangeListener
    lateinit var keyboardManagement: KeyboardManagement

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = activity?.run {
            ViewModelProvider(this, providerFactory).get(AccountViewModel::class.java)
        } ?: throw Exception("Invalid Activity")
        setupActionBarWithNavController(R.id.accountFragment, activity as AppCompatActivity)
        cancelActiveJobs()
    }

    fun cancelActiveJobs() {
        viewModel.cancelActiveJobs()
    }

    fun setupActionBarWithNavController(fragmentId: Int, activity: AppCompatActivity) {
        val appBarConfiguration = AppBarConfiguration(setOf(fragmentId))
        NavigationUI.setupActionBarWithNavController(
            activity,
            findNavController(),
            appBarConfiguration
        )
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            stateChangeListener = context as DataStateChangeListener
        } catch (e: ClassCastException) {
            Log.e(TAG, "$context must implement DataStateChangeListener")
        }
        try {
            keyboardManagement = context as KeyboardManagement
        } catch (e: ClassCastException) {
            Log.e(TAG, "$context must implement KeyboardManagementInterface")
        }
    }
}