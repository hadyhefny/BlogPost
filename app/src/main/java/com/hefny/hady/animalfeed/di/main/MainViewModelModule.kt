package com.hefny.hady.animalfeed.di.main

import androidx.lifecycle.ViewModel
import com.hefny.hady.animalfeed.di.ViewModelKey
import com.hefny.hady.animalfeed.ui.main.account.AccountViewModel
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