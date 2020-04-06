package com.hefny.hady.blogpost.di.auth

import com.hefny.hady.blogpost.ui.auth.ForgotPasswordFragment
import com.hefny.hady.blogpost.ui.auth.LauncherFragment
import com.hefny.hady.blogpost.ui.auth.LoginFragment
import com.hefny.hady.blogpost.ui.auth.RegisterFragment
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