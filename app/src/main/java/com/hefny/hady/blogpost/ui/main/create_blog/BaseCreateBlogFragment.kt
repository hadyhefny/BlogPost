package com.hefny.hady.blogpost.ui.main.create_blog

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import com.hefny.hady.blogpost.R
import com.hefny.hady.blogpost.ui.KeyboardManagement
import com.hefny.hady.blogpost.ui.StoragePermissionInterface
import com.hefny.hady.blogpost.ui.UICommunicationListener
import kotlinx.coroutines.FlowPreview

@FlowPreview
abstract class BaseCreateBlogFragment
constructor(
    @LayoutRes
    private val layoutRes: Int,
    private val viewModelFactory: ViewModelProvider.Factory
) : Fragment(layoutRes) {
    val TAG: String = "AppDebug"
    lateinit var uiCommunicationListener: UICommunicationListener
    lateinit var storagePermissionInterface: StoragePermissionInterface
    lateinit var keyboardManagement: KeyboardManagement

    val viewModel: CreateBlogViewModel by viewModels {
        viewModelFactory
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupActionBarWithNavController(R.id.createBlogFragment, activity as AppCompatActivity)
        setupChannel()
    }

    private fun setupChannel() = viewModel.setupChannel()

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