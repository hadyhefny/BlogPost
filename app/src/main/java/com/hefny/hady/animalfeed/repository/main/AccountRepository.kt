package com.hefny.hady.animalfeed.repository.main

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import com.hefny.hady.animalfeed.api.main.OpenApiMainService
import com.hefny.hady.animalfeed.models.AccountProperties
import com.hefny.hady.animalfeed.models.AuthToken
import com.hefny.hady.animalfeed.persistence.AccountPropertiesDao
import com.hefny.hady.animalfeed.repository.NetworkBoundResource
import com.hefny.hady.animalfeed.session.SessionManager
import com.hefny.hady.animalfeed.ui.DataState
import com.hefny.hady.animalfeed.ui.main.account.state.AccountViewState
import com.hefny.hady.animalfeed.util.ApiSuccessResponse
import com.hefny.hady.animalfeed.util.GenericApiResponse
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.Job
import kotlinx.coroutines.withContext
import javax.inject.Inject

class AccountRepository
@Inject
constructor(
    val openApiMainService: OpenApiMainService,
    val accountPropertiesDao: AccountPropertiesDao,
    val sessionManager: SessionManager
) {
    private val TAG = "AppDebug"
    private var repositoryJob: Job? = null

    fun getAccountProperties(authToken: AuthToken): LiveData<DataState<AccountViewState>> {
        return object :
            NetworkBoundResource<AccountProperties, AccountProperties, AccountViewState>(
                sessionManager.isConnectedToTheInternet(),
                true,
                true
            ) {
            override suspend fun createCacheRequestAndReturn() {
                withContext(Main) {
                    // finishing by viewing db cache
                    result.addSource(loadFromCache()) { viewState ->
                        onCompleteJob(DataState.data(viewState, null))
                    }
                }
            }

            override suspend fun handleApiSuccessResponse(response: ApiSuccessResponse<AccountProperties>) {
                updateLocalDb(response.body)
                createCacheRequestAndReturn()
            }

            override fun createCall(): LiveData<GenericApiResponse<AccountProperties>> {
                return openApiMainService.getAccountProperties(
                    "Token ${authToken.token}"
                )
            }

            override fun setJob(job: Job) {
                repositoryJob?.cancel()
                repositoryJob = job
            }

            override fun loadFromCache(): LiveData<AccountViewState> {
                return Transformations.switchMap(accountPropertiesDao.searchByPk(authToken.account_pk!!)) {
                    object : LiveData<AccountViewState>() {
                        override fun onActive() {
                            super.onActive()
                            value = AccountViewState(it)
                        }
                    }
                }
            }

            override suspend fun updateLocalDb(cacheObject: AccountProperties?) {
                cacheObject?.let {
                    accountPropertiesDao.updateAccountProperties(
                        cacheObject.email,
                        cacheObject.username,
                        cacheObject.pk
                    )
                }
            }
        }.asLiveData()
    }

    fun cancelActiveJobs() {
        Log.d(TAG, "AccountRepository: canceling on-going jobs...")
        repositoryJob?.cancel()
    }
}