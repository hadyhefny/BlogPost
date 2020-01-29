package com.hefny.hady.animalfeed.ui.auth

import androidx.lifecycle.ViewModel
import com.hefny.hady.animalfeed.modelsTest.User
import com.hefny.hady.animalfeed.repository.auth.AuthRepository

class AuthViewModel
constructor(
    val authRepository: AuthRepository
) : ViewModel() {

    init {
        println("DEBUG: viewModel CALLED")
    }

    suspend fun getUsers(): List<User> {
        return authRepository.getUsers()
    }

}