package com.hefny.hady.blogpost.di.auth

import com.hefny.hady.blogpost.ui.auth.AuthActivity
import dagger.Subcomponent

@AuthScope
@Subcomponent(
    modules = [
        AuthModule::class,
        AuthViewModelModule::class,
        AuthFragmentsModule::class
    ]
)
interface AuthComponent {
    @Subcomponent.Factory
    interface Factory {
        fun create(): AuthComponent
    }

    fun inject(authActivity: AuthActivity)
}