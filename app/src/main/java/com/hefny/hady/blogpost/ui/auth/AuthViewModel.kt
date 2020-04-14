package com.hefny.hady.blogpost.ui.auth

import androidx.lifecycle.LiveData
import com.hefny.hady.blogpost.models.AuthToken
import com.hefny.hady.blogpost.repository.auth.AuthRepository
import com.hefny.hady.blogpost.ui.BaseViewModel
import com.hefny.hady.blogpost.ui.DataState
import com.hefny.hady.blogpost.ui.auth.state.AuthStateEvent
import com.hefny.hady.blogpost.ui.auth.state.AuthViewState
import com.hefny.hady.blogpost.ui.auth.state.LoginFields
import com.hefny.hady.blogpost.ui.auth.state.RegistrationFields
import javax.inject.Inject

class AuthViewModel
@Inject
constructor(
    private val authRepository: AuthRepository
) : BaseViewModel<AuthStateEvent, AuthViewState>() {
    override fun handleStateEvent(stateEvent: AuthStateEvent): LiveData<DataState<AuthViewState>> {
        return when (stateEvent) {
            is AuthStateEvent.LoginAttemptEvent -> {
                authRepository.attemptLogin(stateEvent.email, stateEvent.password)
            }
            is AuthStateEvent.RegisterAttemptEvent -> {
                authRepository.attemptRegister(
                    stateEvent.email,
                    stateEvent.userName,
                    stateEvent.password,
                    stateEvent.confirm_password
                )
            }
            is AuthStateEvent.CheckPreviousAuthEvent -> {
                authRepository.checkPreviousAuthUser()
            }
            is AuthStateEvent.None -> {
                object : LiveData<DataState<AuthViewState>>() {
                    override fun onActive() {
                        super.onActive()
                        value = DataState.data(null, null)
                    }
                }
            }
        }
    }

    override fun initNewViewState(): AuthViewState {
        return AuthViewState()
    }

    fun setRegistrationFields(registrationFields: RegistrationFields) {
        val update = getCurrentViewStateOrNew()
        if (update.registrationFields == registrationFields) {
            return
        }
        update.registrationFields = registrationFields
        setViewState(update)
    }

    fun setLoginFields(loginFields: LoginFields) {
        val update = getCurrentViewStateOrNew()
        if (update.loginFields == loginFields) {
            return
        }
        update.loginFields = loginFields
        setViewState(update)
    }

    fun setAuthToken(authToken: AuthToken) {
        val update = getCurrentViewStateOrNew()
        if (update.authToken == authToken) {
            return
        }
        update.authToken = authToken
        setViewState(update)
    }

    fun cancelActiveJobs() {
        handlePendingJobs()
        authRepository.cancelActiveJobs()
    }

    // hide progress bar
    private fun handlePendingJobs() {
        setStateEvent(AuthStateEvent.None())
    }

    override fun onCleared() {
        super.onCleared()
        cancelActiveJobs()
    }
}