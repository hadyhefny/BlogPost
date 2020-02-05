package com.hefny.hady.animalfeed.ui

import com.hefny.hady.animalfeed.session.SessionManager
import dagger.android.support.DaggerAppCompatActivity
import javax.inject.Inject

abstract class BaseActivity : DaggerAppCompatActivity() {
    val TAG = "AppDebug"

    @Inject
    lateinit var sessionManager: SessionManager
}