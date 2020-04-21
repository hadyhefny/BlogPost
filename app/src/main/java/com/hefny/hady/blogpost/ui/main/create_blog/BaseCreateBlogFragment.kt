package com.hefny.hady.blogpost.ui.main.create_blog

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
import com.bumptech.glide.RequestManager
import com.hefny.hady.blogpost.R
import com.hefny.hady.blogpost.di.Injectable
import com.hefny.hady.blogpost.ui.DataStateChangeListener
import com.hefny.hady.blogpost.ui.KeyboardManagement
import com.hefny.hady.blogpost.ui.StoragePermissionInterface
import com.hefny.hady.blogpost.ui.UICommunicationListener
import com.hefny.hady.blogpost.viewmodels.ViewModelProviderFactory
import javax.inject.Inject

abstract class BaseCreateBlogFragment : Fragment(), Injectable {
    val TAG: String = "AppDebug"
    lateinit var uiCommunicationListener: UICommunicationListener
    lateinit var stateChangeListener: DataStateChangeListener
    lateinit var viewModel: CreateBlogViewModel
    lateinit var storagePermissionInterface: StoragePermissionInterface
    lateinit var keyboardManagement: KeyboardManagement

    @Inject
    lateinit var requestManager: RequestManager

    @Inject
    lateinit var providerFactory: ViewModelProviderFactory

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupActionBarWithNavController(R.id.createBlogFragment, activity as AppCompatActivity)
        viewModel = activity?.run {
            ViewModelProvider(this, providerFactory).get(CreateBlogViewModel::class.java)
        } ?: throw Exception("Invalid Activity")
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
            uiCommunicationListener = context as UICommunicationListener
        } catch (e: ClassCastException) {
            Log.e(TAG, "$context must implement UICommunicationListener")
        }
        try {
            storagePermissionInterface = context as StoragePermissionInterface
        } catch (e: ClassCastException) {
            Log.e(TAG, "$context must implement StoragePermissionInterface")
        }
        try {
            keyboardManagement = context as KeyboardManagement
        } catch (e: ClassCastException) {
            Log.e(TAG, "$context must implement KeyboardManagement")
        }
    }
}