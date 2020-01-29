package com.hefny.hady.animalfeed.di

import com.hefny.hady.animalfeed.KodeinViewModelFactory
import com.hefny.hady.animalfeed.ui.auth.AuthViewModel
import org.kodein.di.Kodein
import org.kodein.di.generic.bind
import org.kodein.di.generic.instance
import org.kodein.di.generic.provider

val viewmodelModule = Kodein.Module("ViewModel") {
    bind() from provider {
        KodeinViewModelFactory(kodein)
    }
    bind() from provider {
        AuthViewModel(authRepository = instance())
    }
}