package com.hefny.hady.animalfeed.session

import android.app.Application
import com.hefny.hady.animalfeed.persistence.AuthTokenDao

class SessionManager
constructor(
    val authTokenDao: AuthTokenDao,
    val application: Application
) {
}