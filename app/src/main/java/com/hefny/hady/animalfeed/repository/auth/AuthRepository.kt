package com.hefny.hady.animalfeed.repository.auth

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations.switchMap
import com.hefny.hady.animalfeed.api.OpenApiAuthService
import com.hefny.hady.animalfeed.models.AuthToken
import com.hefny.hady.animalfeed.persistence.AccountPropertiesDao
import com.hefny.hady.animalfeed.persistence.AuthTokenDao
import com.hefny.hady.animalfeed.session.SessionManager
import com.hefny.hady.animalfeed.ui.DataState
import com.hefny.hady.animalfeed.ui.Response
import com.hefny.hady.animalfeed.ui.ResponseType
import com.hefny.hady.animalfeed.ui.auth.state.AuthViewState
import com.hefny.hady.animalfeed.util.ApiEmptyResponse
import com.hefny.hady.animalfeed.util.ApiErrorResponse
import com.hefny.hady.animalfeed.util.ApiSuccessResponse
import com.hefny.hady.animalfeed.util.ErrorHandling.Companion.UNKNOWN_ERROR
import javax.inject.Inject

class AuthRepository
@Inject
constructor(
    val authTokenDao: AuthTokenDao,
    val accountPropertiesDao: AccountPropertiesDao,
    val openApiAuthService: OpenApiAuthService,
    val sessionManager: SessionManager
) {
    fun attemptLogin(email: String, password: String): LiveData<DataState<AuthViewState>> {
        return switchMap(openApiAuthService.login(email, password)) { response ->
            object : LiveData<DataState<AuthViewState>>() {
                override fun onActive() {
                    super.onActive()
                    when (response) {
                        is ApiSuccessResponse -> {
                            value = DataState.data(
                                AuthViewState(
                                    authToken = AuthToken(
                                        response.body.pk,
                                        response.body.token
                                    )
                                ),
                                response = null
                            )
                        }
                        is ApiErrorResponse -> {
                            value = DataState.error(
                                Response(
                                    message = response.errorMessage,
                                    responseType = ResponseType.Dialog()
                                )
                            )
                        }
                        is ApiEmptyResponse -> {
                            value = DataState.error(
                                Response(
                                    message = UNKNOWN_ERROR,
                                    responseType = ResponseType.Dialog()
                                )
                            )
                        }
                    }
                }
            }
        }
    }

    fun attemptRegister(
        email: String,
        username: String,
        password: String,
        confirmPassword: String
    ): LiveData<DataState<AuthViewState>> {
        return switchMap(
            openApiAuthService.register(email, username, password, confirmPassword)
        ) { response ->
            object : LiveData<DataState<AuthViewState>>() {
                override fun onActive() {
                    super.onActive()
                    when (response) {
                        is ApiSuccessResponse -> {
                            value = DataState.data(
                                AuthViewState(
                                    authToken = AuthToken(
                                        response.body.pk, response.body.token
                                    )
                                )
                            )
                        }
                        is ApiErrorResponse -> {
                            value = DataState.error(
                                Response(
                                    message = response.errorMessage,
                                    responseType = ResponseType.Dialog()
                                )
                            )
                        }
                        is ApiEmptyResponse -> {
                            value = DataState.error(
                                Response(
                                    message = UNKNOWN_ERROR,
                                    responseType = ResponseType.Dialog()
                                )
                            )
                        }
                    }
                }
            }
        }
    }
}