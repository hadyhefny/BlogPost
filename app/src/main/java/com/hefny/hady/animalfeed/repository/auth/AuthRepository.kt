package com.hefny.hady.animalfeed.repository.auth

import android.util.Log
import androidx.lifecycle.LiveData
import com.hefny.hady.animalfeed.api.OpenApiAuthService
import com.hefny.hady.animalfeed.api.network_reponses.LoginResponse
import com.hefny.hady.animalfeed.api.network_reponses.RegistrationResponse
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
import com.hefny.hady.animalfeed.util.ApiSuccessResponse
import com.hefny.hady.animalfeed.util.ErrorHandling
import com.hefny.hady.animalfeed.util.GenericApiResponse
import kotlinx.coroutines.Job
import javax.inject.Inject

class AuthRepository
@Inject
constructor(
    val authTokenDao: AuthTokenDao,
    val accountPropertiesDao: AccountPropertiesDao,
    val openApiAuthService: OpenApiAuthService,
    val sessionManager: SessionManager
) {
    private val TAG = "AppDebug"
    private var repositoryJob: Job? = null
    fun attemptLogin(email: String, password: String): LiveData<DataState<AuthViewState>> {
        val loginFieldsError = LoginFields(email, password).isValidForLogin()
        if (!loginFieldsError.equals(LoginFields.LoginError.none())) {
            Log.d(TAG, "attemptLogin: $loginFieldsError")
            return returnErrorResponse(loginFieldsError, ResponseType.Dialog())
        }
        return object : NetworkBoundResource<LoginResponse, AuthViewState>(
            sessionManager.isConnectedToTheInternet()
        ) {
            override suspend fun handleApiSuccessResponse(response: ApiSuccessResponse<LoginResponse>) {
                Log.d(TAG, "handleApiSuccessResponse: ${response.body.response}")
                // Incorrect login credentials counts as a 200 response from server, so need to handle that
                if (response.body.response.equals(ErrorHandling.GENERIC_AUTH_ERROR)) {
                    return onErrorReturn(response.body.errorMessage, true, false)
                }
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
        }.asLiveData()
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
        return object : NetworkBoundResource<RegistrationResponse, AuthViewState>(
            sessionManager.isConnectedToTheInternet()
        ) {
            override suspend fun handleApiSuccessResponse(response: ApiSuccessResponse<RegistrationResponse>) {
                Log.d(TAG, "handleApiSuccessResponse: ${response.body}")
                // Incorrect login credentials counts as a 200 response from server, so need to handle that
                if (response.body.response.equals(ErrorHandling.GENERIC_AUTH_ERROR)) {
                    return onErrorReturn(response.body.errorMessage, true, false)
                }
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
        }.asLiveData()
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