package com.hefny.hady.animalfeed.session

import android.app.Application
import com.hefny.hady.animalfeed.persistence.AuthTokenDao
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SessionManager
@Inject
constructor(
    val authTokenDao: AuthTokenDao,
    val application: Application
) {
}