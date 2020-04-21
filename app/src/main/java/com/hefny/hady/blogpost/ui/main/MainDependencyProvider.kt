package com.hefny.hady.blogpost.ui.main

import com.bumptech.glide.RequestManager
import com.hefny.hady.blogpost.viewmodels.ViewModelProviderFactory

interface MainDependencyProvider {
    fun getViewModelProviderFactory(): ViewModelProviderFactory
    fun getGlideRequestManager(): RequestManager
}