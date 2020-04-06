package com.hefny.hady.animalfeed.repository.auth

import android.content.SharedPreferences
import android.util.Log
import androidx.lifecycle.LiveData
import com.hefny.hady.animalfeed.api.auth.OpenApiAuthService
import com.hefny.hady.animalfeed.api.auth.network_reponses.LoginResponse
import com.hefny.hady.animalfeed.api.auth.network_reponses.RegistrationResponse
import com.hefny.hady.animalfeed.models.AccountProperties
import com.hefny.hady.animalfeed.models.AuthToken
import com.hefny.hady.animalfeed.persistence.AccountPropertiesDao
import com.hefny.hady.animalfeed.persistence.AuthTokenDao
import com.hefny.hady.animalfeed.repository.NetworkBoundResource
import com.hefny.hady.animalfeed.session.SessionManager
import com.hefny.hady.animalfeed.ui.DataState
import com.hefny.hady.animalfeed.ui.Response
import com.hefny.hady.animalfeed.ui.ResponseType
import com.hefny.hady.animalfeed.ui.auth.state.AuthViewState
import com.hefny.hady.animalfeed.ui.auth.state.LoginFields
import com.hefny.hady.animalfeed.ui.auth.state.RegistrationFields
import com.hefny.hady.animalfeed.util.*
import kotlinx.coroutines.Job
import javax.inject.Inject

class AuthRepository
@Inject
constructor(
    val authTokenDao: AuthTokenDao,
    val accountPropertiesDao: AccountPropertiesDao,
    val openApiAuthService: OpenApiAuthService,
    val sessionManager: SessionManager,
    val sharedPreferences: SharedPreferences,
    val editor: SharedPreferences.Editor
) {
    private val TAG = "AppDebug"
    private var repositoryJob: Job? = null
    fun attemptLogin(email: String, password: String): LiveData<DataState<AuthViewState>> {
        val loginFieldsError = LoginFields(email, password).isValidForLogin()
        if (!loginFieldsError.equals(LoginFields.LoginError.none())) {
            Log.d(TAG, "attemptLogin: $loginFieldsError")
            return returnErrorResponse(loginFieldsError, ResponseType.Dialog())
        }
        return object : NetworkBoundResource<LoginResponse, Any, AuthViewState>(
            isNetworkAvailable = sessionManager.isConnectedToTheInternet(),
            isNetworkRequest = true,
            shouldCancelIfNoInternet = true,
            shouldLoadFromCache = false
        ) {
            override suspend fun handleApiSuccessResponse(response: ApiSuccessResponse<LoginResponse>) {
                Log.d(TAG, "handleApiSuccessResponse: ${response.body.response}")
                // Incorrect login credentials counts as a 200 response from server, so need to handle that
                if (response.body.response.equals(ErrorHandling.GENERIC_AUTH_ERROR)) {
                    return onErrorReturn(response.body.errorMessage, true, false)
                }
                // don't care about result here. just insert if it doesn't exist b/c
                // of foreign key relationship with AuthToken
                accountPropertiesDao.insertAndIgnore(
                    AccountProperties(
                        email = response.body.email,
                        pk = response.body.pk,
                        username = ""
                    )
                )
                // will return -1 if failure
                val result = authTokenDao.insert(
                    AuthToken(
                        account_pk = response.body.pk,
                        token = response.body.token
                    )
                )
                if (result < 0) {
                    return onCompleteJob(
                        DataState.error(
                            Response(ErrorHandling.ERROR_SAVE_AUTH_TOKEN, ResponseType.Dialog())
                        )
                    )
                }
                saveAuthenticatedUserToPrefs(email)
                onCompleteJob(
                    DataState.data(
                        AuthViewState(
                            authToken = AuthToken(
                                account_pk = response.body.pk,
                                token = response.body.token
                            )
                        )
                    )
                )
            }

            override fun createCall(): LiveData<GenericApiResponse<LoginResponse>> {
                return openApiAuthService.login(email, password)
            }

            override fun setJob(job: Job) {
                repositoryJob?.cancel()
                repositoryJob = job
            }

            // not used in this case
            override suspend fun createCacheRequestAndReturn() {
            }

            // not used in this case
            override fun loadFromCache(): LiveData<AuthViewState> {
                return AbsentLiveData.create()
            }

            // not used in this case
            override suspend fun updateLocalDb(cacheObject: Any?) {
            }
        }.asLiveData()
    }

    private fun saveAuthenticatedUserToPrefs(email: String) {
        editor.putString(PreferenceKeys.PREVIOUS_AUTH_USER, email)
        editor.apply()
    }

    fun attemptRegister(
        email: String,
        username: String,
        password: String,
        confirmPassword: String
    ): LiveData<DataState<AuthViewState>> {
        val registrationError =
            RegistrationFields(email, username, password, confirmPassword).isValidForRegistration()
        if (!registrationError.equals(RegistrationFields.RegistrationError.none())) {
            Log.d(TAG, "attemptRegister: $registrationError")
            return returnErrorResponse(registrationError, ResponseType.Dialog())
        }
        return object : NetworkBoundResource<RegistrationResponse, Any, AuthViewState>(
            isNetworkAvailable = sessionManager.isConnectedToTheInternet(),
            isNetworkRequest = true,
            shouldCancelIfNoInternet = true,
            shouldLoadFromCache = false
        ) {
            override suspend fun handleApiSuccessResponse(response: ApiSuccessResponse<RegistrationResponse>) {
                Log.d(TAG, "handleApiSuccessResponse: ${response.body}")
                // Incorrect login credentials counts as a 200 response from server, so need to handle that
                if (response.body.response.equals(ErrorHandling.GENERIC_AUTH_ERROR)) {
                    return onErrorReturn(response.body.errorMessage, true, false)
                }
                // will return -1 if failure
                val result1 = accountPropertiesDao.insertAndReplace(
                    AccountProperties(
                        pk = response.body.pk,
                        email = response.body.email,
                        username = response.body.username
                    )
                )
                if (result1 < 0) {
                    onCompleteJob(
                        DataState.error(
                            Response(
                                ErrorHandling.ERROR_SAVE_ACCOUNT_PROPERTIES,
                                ResponseType.Dialog()
                            )
                        )
                    )
                    return
                }
                // will return -1 if failure
                val result2 = authTokenDao.insert(
                    AuthToken(
                        account_pk = response.body.pk,
                        token = response.body.token
                    )
                )
                if (result2 < 0) {
                    onCompleteJob(
                        DataState.error(
                            Response(ErrorHandling.ERROR_SAVE_AUTH_TOKEN, ResponseType.Dialog())
                        )
                    )
                    return
                }
                saveAuthenticatedUserToPrefs(email)
                onCompleteJob(
                    DataState.data(
                        AuthViewState(
                            authToken = AuthToken(
                                account_pk = response.body.pk,
                                token = response.body.token
                            )
                        )
                    )
                )
            }

            override fun createCall(): LiveData<GenericApiResponse<RegistrationResponse>> {
                return openApiAuthService.register(email, username, password, confirmPassword)
            }

            override fun setJob(job: Job) {
                repositoryJob?.cancel()
                repositoryJob = job
            }

            // not used in this case
            override suspend fun createCacheRequestAndReturn() {
            }

            // not used in this case
            override fun loadFromCache(): LiveData<AuthViewState> {
                return AbsentLiveData.create()
            }

            // not used in this case
            override suspend fun updateLocalDb(cacheObject: Any?) {
            }
        }.asLiveData()
    }

    fun checkPreviousAuthUser(): LiveData<DataState<AuthViewState>> {
        val previousAuthUserEmail: String? =
            sharedPreferences.getString(PreferenceKeys.PREVIOUS_AUTH_USER, null)
        if (previousAuthUserEmail.isNullOrBlank()) {
            Log.d(TAG, "checkPreviousAuthUser: No previously authenticated user found")
            return returnNoTokenFound()
        } else {
            return object : NetworkBoundResource<Void, Any, AuthViewState>(
                isNetworkAvailable = sessionManager.isConnectedToTheInternet(),
                isNetworkRequest = false,
                shouldCancelIfNoInternet = false,
                shouldLoadFromCache = false
            ) {
                override suspend fun createCacheRequestAndReturn() {
                    accountPropertiesDao.searchByEmail(previousAuthUserEmail)
                        ?.let { accountProperties ->
                            if (accountProperties.pk > -1) {
                                authTokenDao.searchByPk(accountProperties.pk)?.let { authToken ->
                                    authToken.token?.let {
                                        onCompleteJob(
                                            DataState.data(
                                                data = AuthViewState(authToken = authToken)
                                            )
                                        )
                                        return
                                    }
                                }
                            }
                        }
                    Log.d(TAG, "createCacheRequestAndReturn: auth token not found")
                    onCompleteJob(
                        DataState.data(
                            null,
                            Response(
                                SuccessHandling.RESPONSE_CHECK_PREVIOUS_AUTH_USER_DONE,
                                ResponseType.None()
                            )
                        )
                    )
                }

                // not used in this case
                override suspend fun handleApiSuccessResponse(response: ApiSuccessResponse<Void>) {
                }

                // not used in this case
                override fun createCall(): LiveData<GenericApiResponse<Void>> {
                    return AbsentLiveData.create()
                }

                override fun setJob(job: Job) {
                    repositoryJob?.cancel()
                    repositoryJob = job
                }

                // not used in this case
                override fun loadFromCache(): LiveData<AuthViewState> {
                    return AbsentLiveData.create()
                }

                // not used in this case
                override suspend fun updateLocalDb(cacheObject: Any?) {
                }

            }.asLiveData()
        }

    }

    private fun returnNoTokenFound(): LiveData<DataState<AuthViewState>> {
        return object : LiveData<DataState<AuthViewState>>() {
            override fun onActive() {
                super.onActive()
                value = DataState.data(
                    null,
                    Response(
                        SuccessHandling.RESPONSE_CHECK_PREVIOUS_AUTH_USER_DONE,
                        ResponseType.None()
                    )
                )
            }
        }
    }

    private fun returnErrorResponse(
        errorMessage: String,
        responseType: ResponseType
    ): LiveData<DataState<AuthViewState>> {
        return object : LiveData<DataState<AuthViewState>>() {
            override fun onActive() {
                super.onActive()
                value = DataState.error(
                    response = Response(
                        message = errorMessage,
                        responseType = responseType
                    )
                )
            }
        }
    }

    fun cancelActiveJobs() {
        Log.d(TAG, "AuthRepository: canceling ongoing jobs")
        repositoryJob?.cancel()
    }
}