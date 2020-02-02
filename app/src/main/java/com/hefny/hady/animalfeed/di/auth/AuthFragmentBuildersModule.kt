package com.hefny.hady.animalfeed.di.auth

import com.hefny.hady.animalfeed.ui.auth.ForgotPasswordFragment
import com.hefny.hady.animalfeed.ui.auth.LauncherFragment
import com.hefny.hady.animalfeed.ui.auth.LoginFragment
import com.hefny.hady.animalfeed.ui.auth.RegisterFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class AuthFragmentBuildersModule {

    @ContributesAndroidInjector()
    abstract fun contributeLauncherFragment(): LauncherFragment

    @ContributesAndroidInjector()
    abstract fun contributeLoginFragment(): LoginFragment

    @ContributesAndroidInjector()
    abstract fun contributeRegisterFragment(): RegisterFragment

    @ContributesAndroidInjector()
    abstract fun contributeForgotPasswordFragment(): ForgotPasswordFragment

}