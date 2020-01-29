package com.hefny.hady.animalfeed.di

import com.hefny.hady.animalfeed.api.FirebaseAuthService
import com.hefny.hady.animalfeed.api.ServiceGenerator
import com.hefny.hady.animalfeed.persistence.AppDatabase
import com.hefny.hady.animalfeed.repository.auth.AuthRepository
import com.hefny.hady.animalfeed.session.SessionManager
import org.kodein.di.Kodein
import org.kodein.di.generic.bind
import org.kodein.di.generic.instance
import org.kodein.di.generic.singleton

val appModule = Kodein.Module("AppModule") {

    // binding retrofit api with retrofit instance
    bind() from singleton {
        ServiceGenerator.getRetrofitInstance()
            .create(FirebaseAuthService::class.java)
    }
    // binding room dao with room instance
    bind() from singleton {
        AppDatabase.RoomInstance.getRoomInstance(context = instance()).getAccountPropertiesDao()
    }
    bind() from singleton {
        AppDatabase.RoomInstance.getRoomInstance(context = instance()).getAuthTokenDao()
    }
    // binding AuthRepository
    bind() from singleton {
        AuthRepository(
            authTokenDao = instance(),
            accountPropertiesDao = instance(),
            firebaseAuthService = instance(),
            sessionManager = instance()
        )
    }
    // binding SessionManager
    bind() from singleton {
        SessionManager(authTokenDao = instance(), application = instance())
    }
}