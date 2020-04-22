package com.hefny.hady.blogpost.ui.main.account

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.hefny.hady.blogpost.R
import com.hefny.hady.blogpost.di.main.MainScope
import com.hefny.hady.blogpost.ui.main.account.state.ACCOUNT_VIEW_STATE_BUNDLE_KEY
import com.hefny.hady.blogpost.ui.main.account.state.AccountStateEvent
import com.hefny.hady.blogpost.ui.main.account.state.AccountViewState
import com.hefny.hady.blogpost.util.SuccessHandling
import kotlinx.android.synthetic.main.fragment_change_password.*
import javax.inject.Inject

@MainScope
class ChangePasswordFragment
@Inject
constructor(
    private val viewModelFactory: ViewModelProvider.Factory
) : BaseAccountFragment(R.layout.fragment_change_password) {
    val viewModel: AccountViewModel by viewModels {
        viewModelFactory
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        cancelActiveJobs()
        // restore state after process death
        savedInstanceState?.let { inState ->
            (inState[ACCOUNT_VIEW_STATE_BUNDLE_KEY] as AccountViewState?)?.let { viewState ->
                viewModel.setViewState(viewState)
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putParcelable(ACCOUNT_VIEW_STATE_BUNDLE_KEY, viewModel.viewState.value)
        super.onSaveInstanceState(outState)
    }

    override fun cancelActiveJobs() {
        viewModel.cancelActiveJobs()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        update_password_button.setOnClickListener {
            viewModel.setStateEvent(
                AccountStateEvent.ChangePasswordEvent(
                    input_current_password.text.toString(),
                    input_new_password.text.toString(),
                    input_confirm_new_password.text.toString()
                )
            )
        }
        subscribeObservers()
    }

    private fun subscribeObservers() {
        viewModel.dataState.observe(viewLifecycleOwner, Observer { dataState ->
            if (dataState != null) {
                stateChangeListener.onDataStateChange(dataState)
                Log.d(TAG, "ChangePasswordFragment, dataState: ${dataState}")
                dataState.data?.let { data ->
                    data.response?.let { event ->
                        if (event.peekContent().message
                                .equals(
                                    SuccessHandling.RESPONSE_PASSWORD_UPDATE_SUCCESS
                                )
                        ) {
                            keyboardManagement.hideSoftKeyboard()
                            findNavController().popBackStack()
                        }
                    }
                }
            }
        })
    }
}