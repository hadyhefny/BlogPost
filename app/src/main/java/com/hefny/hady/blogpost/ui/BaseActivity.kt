package com.hefny.hady.blogpost.ui

import android.content.Context
import android.util.Log
import android.view.inputmethod.InputMethodManager
import com.google.android.material.appbar.AppBarLayout
import com.hefny.hady.blogpost.R
import com.hefny.hady.blogpost.session.SessionManager
import dagger.android.support.DaggerAppCompatActivity
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import javax.inject.Inject

abstract class BaseActivity : DaggerAppCompatActivity(), DataStateChangeListener,
    KeyboardManagement, AppbarManagement {
    val TAG = "AppDebug"

    @Inject
    lateinit var sessionManager: SessionManager

    override fun onDataStateChange(dataState: DataState<*>?) {
        dataState?.let {
            GlobalScope.launch(Main) {
                displayProgressBar(it.loading.isLoading)
                it.error?.let { stateError ->
                    handleStateError(stateError)
                }
                it.data?.let {
                    it.response?.let { response ->
                        handleStateSuccess(response)
                    }
                }
            }
        }
    }

    private fun handleStateSuccess(event: Event<Response>) {
        event.getContentIfNotHandled()?.let {
            when (it.responseType) {
                is ResponseType.Toast -> {
                    it.message?.let { message ->
                        displayToast(message)
                    }
                }
                is ResponseType.Dialog -> {
                    it.message?.let { message ->
                        displaySuccessDialog(message)
                    }
                }
                is ResponseType.None -> {
                    it.message?.let { message ->
                        Log.e(TAG, "handleStateSuccess: $message")
                    }
                }
            }
        }
    }

    private fun handleStateError(event: Event<StateError>) {
        event.getContentIfNotHandled()?.let {
            when (it.response.responseType) {
                is ResponseType.Toast -> {
                    it.response.message?.let { message ->
                        displayToast(message)
                    }
                }
                is ResponseType.Dialog -> {
                    it.response.message?.let { message ->
                        displayErrorDialog(message)
                    }
                }
                is ResponseType.None -> {
                    it.response.message?.let { message ->
                        Log.e(TAG, "handleStateError: $message")
                    }
                }
            }
        }
    }

    override fun hideSoftKeyboard() {
        if (currentFocus != null) {
            val inputMethodManager = getSystemService(
                Context.INPUT_METHOD_SERVICE
            ) as InputMethodManager
            inputMethodManager.hideSoftInputFromWindow(currentFocus!!.windowToken, 0)
        }
    }

    override fun expandAppBar() {
        findViewById<AppBarLayout>(R.id.app_bar).setExpanded(true)
    }

    abstract fun displayProgressBar(loading: Boolean)
}