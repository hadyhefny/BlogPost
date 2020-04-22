package com.hefny.hady.blogpost.di

import com.hefny.hady.blogpost.di.auth.AuthComponent
import com.hefny.hady.blogpost.di.main.MainComponent
import dagger.Module

@Module(
    subcomponents = [
        AuthComponent::class,
        MainComponent::class
    ]
)
class SubComponentsModule