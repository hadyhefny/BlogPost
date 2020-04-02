package com.hefny.hady.animalfeed.repository.main

import android.util.Log
import com.hefny.hady.animalfeed.api.main.OpenApiMainService
import com.hefny.hady.animalfeed.persistence.AccountPropertiesDao
import com.hefny.hady.animalfeed.session.SessionManager
import kotlinx.coroutines.Job
import javax.inject.Inject

class AccountRepository
@Inject
constructor(
    val openApiMainService: OpenApiMainService,
    val accountPropertiesDao: AccountPropertiesDao,
    val sessionManager: SessionManager
){
    private val TAG = "AppDebug"
    private var repositoryJob: Job? = null

    fun cancelActiveJobs(){
        Log.d(TAG, "AccountRepository: canceling on-going jobs...")
    }
}