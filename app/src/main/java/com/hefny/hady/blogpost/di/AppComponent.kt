package com.hefny.hady.blogpost.di

import android.app.Application
import com.hefny.hady.blogpost.di.auth.AuthComponent
import com.hefny.hady.blogpost.di.main.MainComponent
import com.hefny.hady.blogpost.session.SessionManager
import com.hefny.hady.blogpost.ui.BaseActivity
import dagger.BindsInstance
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(
    modules = [
        AppModule::class,
        SubComponentsModule::class
    ]
)
interface AppComponent {

    val sessionManager: SessionManager // must add because inject into abstract class

    @Component.Builder
    interface Builder {

        @BindsInstance
        fun application(application: Application): Builder

        fun build(): AppComponent
    }

    fun inject(baseActivity: BaseActivity)
    fun authComponent(): AuthComponent.Factory
    fun mainComponent(): MainComponent.Factory
}