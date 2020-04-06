package com.hefny.hady.animalfeed.di.main

import com.hefny.hady.animalfeed.api.main.OpenApiMainService
import com.hefny.hady.animalfeed.persistence.AccountPropertiesDao
import com.hefny.hady.animalfeed.repository.main.AccountRepository
import com.hefny.hady.animalfeed.session.SessionManager
import dagger.Module
import dagger.Provides
import retrofit2.Retrofit

@Module
class MainModule {

    @MainScope
    @Provides
    fun providesOpenApiMainService(retrofitBuilder: Retrofit.Builder): OpenApiMainService {
        return retrofitBuilder
            .build().create(OpenApiMainService::class.java)
    }

    @MainScope
    @Provides
    fun provideAccountRepository(
        openApiMainService: OpenApiMainService,
        accountPropertiesDao: AccountPropertiesDao,
        sessionManager: SessionManager
    ): AccountRepository {
        return AccountRepository(
            openApiMainService,
            accountPropertiesDao,
            sessionManager
        )
    }
}