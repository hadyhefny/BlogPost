package com.hefny.hady.animalfeed.ui.auth

import androidx.lifecycle.LiveData
import com.hefny.hady.animalfeed.models.AuthToken
import com.hefny.hady.animalfeed.repository.auth.AuthRepository
import com.hefny.hady.animalfeed.ui.BaseViewModel
import com.hefny.hady.animalfeed.ui.DataState
import com.hefny.hady.animalfeed.ui.auth.state.AuthStateEvent
import com.hefny.hady.animalfeed.ui.auth.state.AuthViewState
import com.hefny.hady.animalfeed.ui.auth.state.LoginFields
import com.hefny.hady.animalfeed.ui.auth.state.RegistrationFields
import com.hefny.hady.animalfeed.util.AbsentLiveData
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
        _viewState.value = update
    }

    fun setLoginFields(loginFields: LoginFields) {
        val update = getCurrentViewStateOrNew()
        if (update.loginFields == loginFields) {
            return
        }
        update.loginFields = loginFields
        _viewState.value = update
    }

    fun setAuthToken(authToken: AuthToken) {
        val update = getCurrentViewStateOrNew()
        if (update.authToken == authToken) {
            return
        }
        update.authToken = authToken
        _viewState.value = update
    }

    fun cancelActiveJobs() {
        authRepository.cancelActiveJobs()
    }

    override fun onCleared() {
        super.onCleared()
        cancelActiveJobs()
    }
}