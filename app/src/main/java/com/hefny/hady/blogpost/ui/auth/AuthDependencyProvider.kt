package com.hefny.hady.blogpost.ui.auth

import com.hefny.hady.blogpost.viewmodels.ViewModelProviderFactory

interface AuthDependencyProvider {
    fun getViewModelProviderFactory(): ViewModelProviderFactory
}