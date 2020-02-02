package com.hefny.hady.animalfeed.ui.auth

import androidx.lifecycle.ViewModel
import com.hefny.hady.animalfeed.repository.auth.AuthRepository
import javax.inject.Inject

class AuthViewModel
@Inject
constructor(
    val authRepository: AuthRepository
) : ViewModel() {

}