package com.hefny.hady.blogpost.di.main

import androidx.lifecycle.ViewModel
import com.hefny.hady.blogpost.di.ViewModelKey
import com.hefny.hady.blogpost.ui.main.account.AccountViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
abstract class MainViewModelModule {

    @Binds
    @IntoMap
    @ViewModelKey(AccountViewModel::class)
    abstract fun bindAccountViewModel(accountViewModel: AccountViewModel): ViewModel

}