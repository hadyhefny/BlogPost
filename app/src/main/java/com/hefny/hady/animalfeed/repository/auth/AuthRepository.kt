package com.hefny.hady.animalfeed.repository.auth

import androidx.lifecycle.LiveData
import com.hefny.hady.animalfeed.api.OpenApiAuthService
import com.hefny.hady.animalfeed.api.network_reponses.LoginResponse
import com.hefny.hady.animalfeed.api.network_reponses.RegistrationResponse
import com.hefny.hady.animalfeed.persistence.AccountPropertiesDao
import com.hefny.hady.animalfeed.persistence.AuthTokenDao
import com.hefny.hady.animalfeed.session.SessionManager
import com.hefny.hady.animalfeed.util.GenericApiResponse
import javax.inject.Inject

class AuthRepository
@Inject
constructor(
    val authTokenDao: AuthTokenDao,
    val accountPropertiesDao: AccountPropertiesDao,
    val openApiAuthService: OpenApiAuthService,
    val sessionManager: SessionManager
) {
    fun testLogin(username: String, password: String): LiveData<GenericApiResponse<LoginResponse>> {
        return openApiAuthService.login(username, password)
    }

    fun testRegister(
        email: String,
        username: String,
        password: String,
        confirmPassword: String
    ): LiveData<GenericApiResponse<RegistrationResponse>> {
        return openApiAuthService.register(email, username, password, confirmPassword)
    }
}