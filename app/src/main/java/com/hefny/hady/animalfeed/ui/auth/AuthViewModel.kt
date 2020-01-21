package com.hefny.hady.animalfeed.ui.auth

import androidx.lifecycle.ViewModel
import com.hefny.hady.animalfeed.repository.auth.AuthRepository

class AuthViewModel
constructor(
    val authRepository: AuthRepository
) : ViewModel() {

}