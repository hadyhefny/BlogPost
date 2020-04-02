package com.hefny.hady.animalfeed.di.auth

import android.content.SharedPreferences
import com.hefny.hady.animalfeed.api.auth.OpenApiAuthService
import com.hefny.hady.animalfeed.persistence.AccountPropertiesDao
import com.hefny.hady.animalfeed.persistence.AuthTokenDao
import com.hefny.hady.animalfeed.repository.auth.AuthRepository
import com.hefny.hady.animalfeed.session.SessionManager
import dagger.Module
import dagger.Provides
import retrofit2.Retrofit

@Module
class AuthModule {

    @AuthScope
    @Provides
    fun provideApiService(retrofitBuilder: Retrofit.Builder): OpenApiAuthService {
        return retrofitBuilder
            .build()
            .create(OpenApiAuthService::class.java)
    }

    @AuthScope
    @Provides
    fun provideAuthRepository(
        sessionManager: SessionManager,
        authTokenDao: AuthTokenDao,
        accountPropertiesDao: AccountPropertiesDao,
        openApiAuthService: OpenApiAuthService,
        sharedPreferences: SharedPreferences,
        editor: SharedPreferences.Editor
    ): AuthRepository {
        return AuthRepository(
            authTokenDao,
            accountPropertiesDao,
            openApiAuthService,
            sessionManager,
            sharedPreferences,
            editor
        )
    }
}