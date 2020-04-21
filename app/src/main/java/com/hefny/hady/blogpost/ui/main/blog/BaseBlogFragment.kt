package com.hefny.hady.blogpost.ui.main.blog

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
import com.hefny.hady.blogpost.ui.*
import com.hefny.hady.blogpost.ui.main.MainDependencyProvider
import com.hefny.hady.blogpost.ui.main.blog.state.BLOG_VIEW_STATE_BUNDLE_KEY
import com.hefny.hady.blogpost.ui.main.blog.state.BlogViewState
import com.hefny.hady.blogpost.ui.main.blog.viewmodel.BlogViewModel

abstract class BaseBlogFragment : Fragment(), Injectable {
    val TAG: String = "AppDebug"
    lateinit var viewModel: BlogViewModel
    lateinit var stateChangeListener: DataStateChangeListener
    lateinit var keyboardManagement: KeyboardManagement
    lateinit var appbarManagement: AppbarManagement
    lateinit var uiCommunicationListener: UICommunicationListener
    lateinit var storagePermissionInterface: StoragePermissionInterface
    lateinit var mainDependencyProvider: MainDependencyProvider

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = activity?.run {
            ViewModelProvider(
                this,
                mainDependencyProvider.getViewModelProviderFactory()
            ).get(BlogViewModel::class.java)
        } ?: throw Exception("Invalid Activity")
        cancelActiveJobs()
        // restore state after process death
        savedInstanceState?.let { inState ->
            (inState[BLOG_VIEW_STATE_BUNDLE_KEY] as BlogViewState?)?.let { viewState ->
                viewModel.setViewState(viewState)
            }
        }
    }

    private fun isViewModelInitialized() = ::viewModel.isInitialized
    override fun onSaveInstanceState(outState: Bundle) {
        if (isViewModelInitialized()) {
            outState.putParcelable(BLOG_VIEW_STATE_BUNDLE_KEY, viewModel.viewState.value)
        }
        super.onSaveInstanceState(outState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupActionBarWithNavController(R.id.blogFragment, activity as AppCompatActivity)
        keyboardManagement.hideSoftKeyboard()
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
        try {
            storagePermissionInterface = context as StoragePermissionInterface
        } catch (e: ClassCastException) {
            Log.d(TAG, "$context must implement StoragePermissionInterface")
        }
        try {
            mainDependencyProvider = context as MainDependencyProvider
        } catch (e: ClassCastException) {
            Log.d(TAG, "$context must implement MainDependencyProvider")
        }
    }
}