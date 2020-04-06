package com.hefny.hady.animalfeed.ui.main.account

import androidx.lifecycle.LiveData
import com.hefny.hady.animalfeed.models.AccountProperties
import com.hefny.hady.animalfeed.repository.main.AccountRepository
import com.hefny.hady.animalfeed.session.SessionManager
import com.hefny.hady.animalfeed.ui.BaseViewModel
import com.hefny.hady.animalfeed.ui.DataState
import com.hefny.hady.animalfeed.ui.main.account.state.AccountStateEvent
import com.hefny.hady.animalfeed.ui.main.account.state.AccountViewState
import com.hefny.hady.animalfeed.util.AbsentLiveData
import javax.inject.Inject

class AccountViewModel
@Inject
constructor(
    val accountRepository: AccountRepository,
    val sessionManager: SessionManager
) : BaseViewModel<AccountStateEvent, AccountViewState>() {
    override fun handleStateEvent(stateEvent: AccountStateEvent): LiveData<DataState<AccountViewState>> {
        return when (stateEvent) {
            is AccountStateEvent.GetAccountPropertiesEvent -> {
                sessionManager.cachedToken.value?.let { authToken ->
                    accountRepository.getAccountProperties(authToken)
                } ?: AbsentLiveData.create()
            }
            is AccountStateEvent.UpdateAccountPropertiesEvent -> {
                sessionManager.cachedToken.value?.let { authToken ->
                    accountRepository.saveAccountProperties(
                        authToken,
                        AccountProperties(
                            authToken.account_pk!!,
                            stateEvent.email,
                            stateEvent.userName
                        )
                    )
                } ?: AbsentLiveData.create()
            }
            is AccountStateEvent.ChangePasswordEvent -> {
                AbsentLiveData.create()
            }
            is AccountStateEvent.None -> {
                AbsentLiveData.create()
            }
        }
    }

    override fun initNewViewState(): AccountViewState {
        return AccountViewState()
    }

    fun setAccountProperties(accountProperties: AccountProperties) {
        val update = getCurrentViewStateOrNew()
        if (update.accountProperties == accountProperties) {
            return
        }
        update.accountProperties = accountProperties
        _viewState.value = update
    }

    fun logout() {
        sessionManager.logout()
    }

}