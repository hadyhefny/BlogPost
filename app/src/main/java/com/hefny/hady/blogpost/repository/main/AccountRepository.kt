package com.hefny.hady.blogpost.repository.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import com.hefny.hady.blogpost.api.GenericResponse
import com.hefny.hady.blogpost.api.main.OpenApiMainService
import com.hefny.hady.blogpost.di.main.MainScope
import com.hefny.hady.blogpost.models.AccountProperties
import com.hefny.hady.blogpost.models.AuthToken
import com.hefny.hady.blogpost.persistence.AccountPropertiesDao
import com.hefny.hady.blogpost.repository.JobManager
import com.hefny.hady.blogpost.repository.NetworkBoundResource
import com.hefny.hady.blogpost.session.SessionManager
import com.hefny.hady.blogpost.ui.DataState
import com.hefny.hady.blogpost.ui.Response
import com.hefny.hady.blogpost.ui.ResponseType
import com.hefny.hady.blogpost.ui.main.account.state.AccountViewState
import com.hefny.hady.blogpost.util.AbsentLiveData
import com.hefny.hady.blogpost.util.ApiSuccessResponse
import com.hefny.hady.blogpost.util.GenericApiResponse
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.Job
import kotlinx.coroutines.withContext
import javax.inject.Inject

@MainScope
class AccountRepository
@Inject
constructor(
    val openApiMainService: OpenApiMainService,
    val accountPropertiesDao: AccountPropertiesDao,
    val sessionManager: SessionManager
) : JobManager("AccountRepository") {
    private val TAG = "AppDebug"
    private var repositoryJob: Job? = null

    fun getAccountProperties(authToken: AuthToken): LiveData<DataState<AccountViewState>> {
        return object :
            NetworkBoundResource<AccountProperties, AccountProperties, AccountViewState>(
                isNetworkAvailable = sessionManager.isConnectedToTheInternet(),
                isNetworkRequest = true,
                shouldCancelIfNoInternet = false,
                shouldLoadFromCache = true
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
                addJob("getAccountProperties", job)
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

    fun saveAccountProperties(
        authToken: AuthToken,
        accountProperties: AccountProperties
    ): LiveData<DataState<AccountViewState>> {
        return object : NetworkBoundResource<GenericResponse, Any, AccountViewState>(
            isNetworkAvailable = sessionManager.isConnectedToTheInternet(),
            isNetworkRequest = true,
            shouldCancelIfNoInternet = true,
            shouldLoadFromCache = false
        ) {
            // not used in this case
            override suspend fun createCacheRequestAndReturn() {
            }

            override suspend fun handleApiSuccessResponse(response: ApiSuccessResponse<GenericResponse>) {
                updateLocalDb(null) // the update doesn't return a cache object
                withContext(Main) {
                    // finish with success response
                    onCompleteJob(
                        DataState.data(
                            data = null,
                            response = Response(response.body.response, ResponseType.Toast())
                        )
                    )
                }
            }

            override fun createCall(): LiveData<GenericApiResponse<GenericResponse>> {
                return openApiMainService.updateAccountProperties(
                    "Token ${authToken.token}",
                    accountProperties.email,
                    accountProperties.username
                )
            }

            // not used in this case
            override fun loadFromCache(): LiveData<AccountViewState> {
                return AbsentLiveData.create()
            }

            override suspend fun updateLocalDb(cacheObject: Any?) {
                accountPropertiesDao.updateAccountProperties(
                    accountProperties.email,
                    accountProperties.username,
                    authToken.account_pk!!
                )
            }

            override fun setJob(job: Job) {
                addJob("saveAccountProperties", job)
            }

        }.asLiveData()
    }

    fun updatePassword(
        authToken: AuthToken,
        currentPassword: String,
        newPassword: String,
        confirmNewPassword: String
    ): LiveData<DataState<AccountViewState>> {
        return object : NetworkBoundResource<GenericResponse, Any, AccountViewState>(
            isNetworkAvailable = sessionManager.isConnectedToTheInternet(),
            isNetworkRequest = true,
            shouldCancelIfNoInternet = true,
            shouldLoadFromCache = false
        ) {
            // not used in this case
            override suspend fun createCacheRequestAndReturn() {
            }

            override suspend fun handleApiSuccessResponse(response: ApiSuccessResponse<GenericResponse>) {
                withContext(Main) {
                    // finish with success response
                    onCompleteJob(
                        DataState.data(
                            data = null,
                            response = Response(response.body.response, ResponseType.Toast())
                        )
                    )
                }
            }

            override fun createCall(): LiveData<GenericApiResponse<GenericResponse>> {
                return openApiMainService.updatePassword(
                    "Token ${authToken.token}",
                    currentPassword,
                    newPassword,
                    confirmNewPassword
                )
            }

            // not used in this case
            override fun loadFromCache(): LiveData<AccountViewState> {
                return AbsentLiveData.create()
            }

            // not used in this case
            override suspend fun updateLocalDb(cacheObject: Any?) {
            }

            override fun setJob(job: Job) {
                addJob("updatePassword", job)
            }
        }.asLiveData()
    }
}