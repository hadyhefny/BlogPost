package com.hefny.hady.blogpost.di.auth

import androidx.fragment.app.FragmentFactory
import androidx.lifecycle.ViewModelProvider
import com.hefny.hady.blogpost.fragments.auth.AuthFragmentFactory
import dagger.Module
import dagger.Provides

@Module
object AuthFragmentsModule {
    @JvmStatic
    @AuthScope
    @Provides
    fun provideAuthFragmentFactory(
        viewModelFactory: ViewModelProvider.Factory
    ): FragmentFactory {
        return AuthFragmentFactory(
            viewModelFactory
        )
    }
}