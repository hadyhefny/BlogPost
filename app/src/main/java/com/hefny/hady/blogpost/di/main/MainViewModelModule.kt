package com.hefny.hady.blogpost.di.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.hefny.hady.blogpost.di.main.keys.MainViewModelKey
import com.hefny.hady.blogpost.ui.main.account.AccountViewModel
import com.hefny.hady.blogpost.ui.main.blog.viewmodel.BlogViewModel
import com.hefny.hady.blogpost.ui.main.create_blog.CreateBlogViewModel
import com.hefny.hady.blogpost.viewmodels.MainViewModelFactory
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
abstract class MainViewModelModule {

    @MainScope
    @Binds
    abstract fun bindMainViewModelFactory(mainViewModelFactory: MainViewModelFactory): ViewModelProvider.Factory

    @MainScope
    @Binds
    @IntoMap
    @MainViewModelKey(AccountViewModel::class)
    abstract fun bindAccountViewModel(accountViewModel: AccountViewModel): ViewModel

    @MainScope
    @Binds
    @IntoMap
    @MainViewModelKey(BlogViewModel::class)
    abstract fun bindBlogViewModel(blogViewModel: BlogViewModel): ViewModel

    @MainScope
    @Binds
    @IntoMap
    @MainViewModelKey(CreateBlogViewModel::class)
    abstract fun bindCreateBlogViewModel(createBlogViewModel: CreateBlogViewModel): ViewModel
}