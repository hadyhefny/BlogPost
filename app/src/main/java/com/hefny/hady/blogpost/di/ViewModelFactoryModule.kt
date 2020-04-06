package com.hefny.hady.blogpost.di

import androidx.lifecycle.ViewModelProvider
import com.hefny.hady.blogpost.viewmodels.ViewModelProviderFactory
import dagger.Binds
import dagger.Module

@Module
abstract class ViewModelFactoryModule {

    @Binds
    abstract fun bindViewModelFactory(factory: ViewModelProviderFactory): ViewModelProvider.Factory
}