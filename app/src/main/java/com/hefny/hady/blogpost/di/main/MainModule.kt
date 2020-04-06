package com.hefny.hady.blogpost.di.main

import com.hefny.hady.blogpost.api.main.OpenApiMainService
import com.hefny.hady.blogpost.persistence.AccountPropertiesDao
import com.hefny.hady.blogpost.repository.main.AccountRepository
import com.hefny.hady.blogpost.session.SessionManager
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