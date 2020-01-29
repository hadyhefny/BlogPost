package com.hefny.hady.animalfeed.repository.auth

import com.hefny.hady.animalfeed.api.FirebaseAuthService
import com.hefny.hady.animalfeed.modelsTest.User
import com.hefny.hady.animalfeed.persistence.AccountPropertiesDao
import com.hefny.hady.animalfeed.persistence.AuthTokenDao
import com.hefny.hady.animalfeed.session.SessionManager

class AuthRepository
constructor(
    val authTokenDao: AuthTokenDao,
    val accountPropertiesDao: AccountPropertiesDao,
    val firebaseAuthService: FirebaseAuthService,
    val sessionManager: SessionManager
) {

    suspend fun getUsers(): List<User> {
        return firebaseAuthService.getUsers()
    }
}