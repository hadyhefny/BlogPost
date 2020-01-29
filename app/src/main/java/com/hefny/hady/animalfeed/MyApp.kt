package com.hefny.hady.animalfeed

import android.app.Application
import com.hefny.hady.animalfeed.di.appModule
import com.hefny.hady.animalfeed.di.viewmodelModule
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.androidXModule

class MyApp : Application(), KodeinAware {
    override val kodein by Kodein.lazy {
        import(androidXModule(this@MyApp))
        import(appModule)
        import(viewmodelModule)
    }
}