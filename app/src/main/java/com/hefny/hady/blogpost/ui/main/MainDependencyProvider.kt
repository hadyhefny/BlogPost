package com.hefny.hady.blogpost.ui.main

import com.bumptech.glide.RequestManager
import com.hefny.hady.blogpost.viewmodels.MainViewModelFactory

interface MainDependencyProvider {
    fun getViewModelProviderFactory(): MainViewModelFactory
    fun getGlideRequestManager(): RequestManager
}