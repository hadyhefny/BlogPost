package com.hefny.hady.blogpost.ui.main.blog

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import com.bumptech.glide.RequestManager
import com.hefny.hady.blogpost.R
import com.hefny.hady.blogpost.ui.AppbarManagement
import com.hefny.hady.blogpost.ui.DataStateChangeListener
import com.hefny.hady.blogpost.ui.KeyboardManagement
import com.hefny.hady.blogpost.ui.UICommunicationListener
import com.hefny.hady.blogpost.ui.main.blog.viewmodel.BlogViewModel
import com.hefny.hady.blogpost.viewmodels.ViewModelProviderFactory
import dagger.android.support.DaggerFragment
import javax.inject.Inject

abstract class BaseBlogFragment : DaggerFragment() {
    val TAG: String = "AppDebug"

    @Inject
    lateinit var providerFactory: ViewModelProviderFactory

    @Inject
    lateinit var requestManager: RequestManager

    lateinit var viewModel: BlogViewModel

    lateinit var stateChangeListener: DataStateChangeListener

    lateinit var keyboardManagement: KeyboardManagement

    lateinit var appbarManagement: AppbarManagement

    lateinit var uiCommunicationListener: UICommunicationListener

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupActionBarWithNavController(R.id.blogFragment, activity as AppCompatActivity)
        viewModel = activity?.run {
            ViewModelProvider(this, providerFactory).get(BlogViewModel::class.java)
        } ?: throw Exception("Invalid Activity")
        keyboardManagement.hideSoftKeyboard()
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
        try {
            appbarManagement = context as AppbarManagement
        } catch (e: ClassCastException) {
            Log.d(TAG, "$context must implement AppbarManagement")
        }
        try {
            uiCommunicationListener = context as UICommunicationListener
        } catch (e: ClassCastException) {
            Log.d(TAG, "$context must implement UICommunicationListener")
        }
    }
}