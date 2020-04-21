package com.hefny.hady.blogpost.ui.auth

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.hefny.hady.blogpost.di.Injectable
import com.hefny.hady.blogpost.ui.auth.state.AUTH_VIEW_STATE_BUNDLE_KEY
import com.hefny.hady.blogpost.ui.auth.state.AuthViewState

abstract class BaseAuthFragment : Fragment(), Injectable {
    private val TAG = "AppDebug"
    lateinit var viewModel: AuthViewModel
    lateinit var authDependencyProvider: AuthDependencyProvider

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = activity?.run {
            ViewModelProvider(
                this,
                authDependencyProvider.getViewModelProviderFactory()
            )
                .get(AuthViewModel::class.java)
        } ?: throw Exception("Invalid Activity")
        cancelActiveJobs()
        // restore state after process death
        savedInstanceState?.let { inState ->
            (inState[AUTH_VIEW_STATE_BUNDLE_KEY] as AuthViewState?)?.let { viewState ->
                viewModel.setViewState(viewState)
            }
        }
    }

    private fun isViewModelInitialized() = ::viewModel.isInitialized
    override fun onSaveInstanceState(outState: Bundle) {
        if (isViewModelInitialized()) {
            outState.putParcelable(AUTH_VIEW_STATE_BUNDLE_KEY, viewModel.viewState.value)
        }
        super.onSaveInstanceState(outState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    private fun cancelActiveJobs() {
        viewModel.cancelActiveJobs()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            authDependencyProvider = context as AuthDependencyProvider
        } catch (e: ClassCastException) {
            Log.e(TAG, "$context must implement AuthDependencyProvider")
        }
    }
}