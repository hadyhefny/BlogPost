package com.hefny.hady.animalfeed.di.main

import com.hefny.hady.animalfeed.ui.main.account.AccountFragment
import com.hefny.hady.animalfeed.ui.main.account.ChangePasswordFragment
import com.hefny.hady.animalfeed.ui.main.account.UpdateAccountFragment
import com.hefny.hady.animalfeed.ui.main.blog.BlogFragment
import com.hefny.hady.animalfeed.ui.main.blog.UpdateBlogFragment
import com.hefny.hady.animalfeed.ui.main.blog.ViewBlogFragment
import com.hefny.hady.animalfeed.ui.main.create_blog.CreateBlogFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class MainFragmentBuildersModule {

    @ContributesAndroidInjector()
    abstract fun contributeBlogFragment(): BlogFragment

    @ContributesAndroidInjector()
    abstract fun contributeAccountFragment(): AccountFragment

    @ContributesAndroidInjector()
    abstract fun contributeChangePasswordFragment(): ChangePasswordFragment

    @ContributesAndroidInjector()
    abstract fun contributeCreateBlogFragment(): CreateBlogFragment

    @ContributesAndroidInjector()
    abstract fun contributeUpdateBlogFragment(): UpdateBlogFragment

    @ContributesAndroidInjector()
    abstract fun contributeViewBlogFragment(): ViewBlogFragment

    @ContributesAndroidInjector()
    abstract fun contributeUpdateAccountFragment(): UpdateAccountFragment
}