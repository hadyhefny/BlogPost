package com.hefny.hady.animalfeed.ui.auth

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.hefny.hady.animalfeed.api.network_reponses.LoginResponse
import com.hefny.hady.animalfeed.api.network_reponses.RegistrationResponse
import com.hefny.hady.animalfeed.repository.auth.AuthRepository
import com.hefny.hady.animalfeed.util.GenericApiResponse
import javax.inject.Inject

class AuthViewModel
@Inject
constructor(
    private val authRepository: AuthRepository
) : ViewModel() {
    fun testLogin(username: String, password: String): LiveData<GenericApiResponse<LoginResponse>> {
        return authRepository.testLogin(username, password)
    }

    fun testRegister(
        email: String,
        username: String,
        password: String,
        confirmPassword: String
    ): LiveData<GenericApiResponse<RegistrationResponse>> {
        return authRepository.testRegister(email, username, password, confirmPassword)
    }
}