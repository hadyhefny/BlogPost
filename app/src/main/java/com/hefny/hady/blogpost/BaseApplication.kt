package com.hefny.hady.blogpost

import android.app.Application
import com.hefny.hady.blogpost.di.AppComponent
import com.hefny.hady.blogpost.di.DaggerAppComponent
import com.hefny.hady.blogpost.di.auth.AuthComponent
import com.hefny.hady.blogpost.di.main.MainComponent

class BaseApplication : Application() {
    lateinit var appComponent: AppComponent
    private var authComponent: AuthComponent? = null
    private var mainComponent: MainComponent? = null
    override fun onCreate() {
        super.onCreate()
        initAppComponent()
    }

    fun mainComponent(): MainComponent {
        if (mainComponent == null) {
            mainComponent = appComponent.mainComponent().create()
        }
        return mainComponent as MainComponent
    }

    fun releaseMainComponent() {
        mainComponent = null
    }

    fun authComponent(): AuthComponent {
        if (authComponent == null) {
            authComponent = appComponent.authComponent().create()
        }
        return authComponent as AuthComponent
    }

    fun releaseAuthComponent() {
        authComponent = null
    }

    fun initAppComponent() {
        appComponent = DaggerAppComponent.builder().application(this).build()
    }
}