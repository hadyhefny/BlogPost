package com.hefny.hady.blogpost.di.main

import com.hefny.hady.blogpost.api.main.OpenApiMainService
import com.hefny.hady.blogpost.persistence.AccountPropertiesDao
import com.hefny.hady.blogpost.persistence.AppDatabase
import com.hefny.hady.blogpost.persistence.BlogPostDao
import com.hefny.hady.blogpost.repository.main.AccountRepository
import com.hefny.hady.blogpost.repository.main.BlogRepository
import com.hefny.hady.blogpost.repository.main.CreateBlogRepository
import com.hefny.hady.blogpost.session.SessionManager
import dagger.Module
import dagger.Provides
import retrofit2.Retrofit

@Module
object MainModule {

    @JvmStatic
    @MainScope
    @Provides
    fun providesOpenApiMainService(retrofitBuilder: Retrofit.Builder): OpenApiMainService {
        return retrofitBuilder
            .build().create(OpenApiMainService::class.java)
    }

    @JvmStatic
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

    @JvmStatic
    @MainScope
    @Provides
    fun provideBlogPostDao(db: AppDatabase): BlogPostDao {
        return db.getBlogPostDao()
    }

    @JvmStatic
    @MainScope
    @Provides
    fun provideBlogRepository(
        openApiMainService: OpenApiMainService,
        blogPostDao: BlogPostDao,
        sessionManager: SessionManager
    ): BlogRepository {
        return BlogRepository(
            openApiMainService,
            blogPostDao,
            sessionManager
        )
    }

    @JvmStatic
    @MainScope
    @Provides
    fun provideCreateBlogRepository(
        openApiMainService: OpenApiMainService,
        blogPostDao: BlogPostDao,
        sessionManager: SessionManager
    ): CreateBlogRepository {
        return CreateBlogRepository(
            openApiMainService,
            blogPostDao,
            sessionManager
        )
    }
}