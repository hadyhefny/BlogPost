package com.hefny.hady.blogpost.ui.main.blog

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import com.hefny.hady.blogpost.R
import com.hefny.hady.blogpost.ui.*

abstract class BaseBlogFragment
constructor(
    @LayoutRes
    private val layoutRes: Int
) : Fragment(layoutRes) {
    val TAG: String = "AppDebug"
    lateinit var stateChangeListener: DataStateChangeListener
    lateinit var keyboardManagement: KeyboardManagement
    lateinit var appbarManagement: AppbarManagement
    lateinit var uiCommunicationListener: UICommunicationListener
    lateinit var storagePermissionInterface: StoragePermissionInterface

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupActionBarWithNavController(R.id.blogFragment, activity as AppCompatActivity)
        keyboardManagement.hideSoftKeyboard()
    }

    abstract fun cancelActiveJobs()

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
    }
}