package com.hefny.hady.animalfeed.di

import com.hefny.hady.animalfeed.di.auth.AuthFragmentBuildersModule
import com.hefny.hady.animalfeed.di.auth.AuthModule
import com.hefny.hady.animalfeed.di.auth.AuthScope
import com.hefny.hady.animalfeed.di.auth.AuthViewModelModule
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

    @ContributesAndroidInjector
    abstract fun contributeMainActivity(): MainActivity

}