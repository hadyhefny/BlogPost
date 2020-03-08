package com.hefny.hady.animalfeed.di

import com.hefny.hady.animalfeed.di.auth.AuthFragmentBuildersModule
import com.hefny.hady.animalfeed.di.auth.AuthModule
import com.hefny.hady.animalfeed.di.auth.AuthScope
import com.hefny.hady.animalfeed.di.auth.AuthViewModelModule
import com.hefny.hady.animalfeed.di.main.MainFragmentBuildersModule
import com.hefny.hady.animalfeed.di.main.MainModule
import com.hefny.hady.animalfeed.di.main.MainScope
import com.hefny.hady.animalfeed.di.main.MainViewModelModule
import com.hefny.hady.animalfeed.ui.auth.AuthActivity
import com.hefny.hady.animalfeed.ui.main.MainActivity
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class ActivityBuildersModule {

    @AuthScope
    @ContributesAndroidInjector(
        modules = [AuthModule::class, AuthFragmentBuildersModule::class, AuthViewModelModule::class]
    )
    abstract fun contributeAuthActivity(): AuthActivity

    @MainScope
    @ContributesAndroidInjector(
        modules = [MainModule::class, MainFragmentBuildersModule::class, MainViewModelModule::class]
    )
    abstract fun contributeMainActivity(): MainActivity
}