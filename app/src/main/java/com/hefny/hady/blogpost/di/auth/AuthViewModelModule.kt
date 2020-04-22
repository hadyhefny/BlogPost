package com.hefny.hady.blogpost.di.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.hefny.hady.blogpost.di.auth.keys.AuthViewModelKey
import com.hefny.hady.blogpost.ui.auth.AuthViewModel
import com.hefny.hady.blogpost.viewmodels.AuthViewModelFactory
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
abstract class AuthViewModelModule {

    @AuthScope
    @Binds
    abstract fun bindViewModelFactory(authViewModelFactory: AuthViewModelFactory): ViewModelProvider.Factory

    @AuthScope
    @Binds
    @IntoMap
    @AuthViewModelKey(AuthViewModel::class)
    abstract fun bindAuthViewModel(authViewModel: AuthViewModel): ViewModel

}