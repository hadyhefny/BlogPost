package com.hefny.hady.animalfeed.di

import androidx.lifecycle.ViewModelProvider
import com.hefny.hady.animalfeed.viewmodels.ViewModelProviderFactory
import dagger.Binds
import dagger.Module

@Module
abstract class ViewModelFactoryModule {

    @Binds
    abstract fun bindViewModelFactory(factory: ViewModelProviderFactory): ViewModelProvider.Factory
}