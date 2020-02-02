package com.hefny.hady.animalfeed.di.auth

import androidx.lifecycle.ViewModel
import com.hefny.hady.animalfeed.di.ViewModelKey
import com.hefny.hady.animalfeed.ui.auth.AuthViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
abstract class AuthViewModelModule {

    @Binds
    @IntoMap
    @ViewModelKey(AuthViewModel::class)
    abstract fun bindAuthViewModel(authViewModel: AuthViewModel): ViewModel

}