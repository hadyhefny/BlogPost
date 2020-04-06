package com.hefny.hady.blogpost.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import com.hefny.hady.blogpost.ui.DataState
import com.hefny.hady.blogpost.ui.Response
import com.hefny.hady.blogpost.ui.ResponseType
import com.hefny.hady.blogpost.util.*
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main

abstract class NetworkBoundResource<ResponseObject, CacheObject, ViewStateType>(
    isNetworkAvailable: Boolean, // is there a network connection?
    isNetworkRequest: Boolean, // is a network request required? (do we need internet connection?)
    shouldCancelIfNoInternet: Boolean, // should this job be canceled id there is no network?
    shouldLoadFromCache: Boolean // should the cached data be loaded?
) {
    private val TAG = "AppDebug"
    protected val result = MediatorLiveData<DataState<ViewStateType>>()
    protected lateinit var job: CompletableJob
    protected lateinit var coroutineScope: CoroutineScope

    init {
        setJob(initNewJob())
        setValue(DataState.loading(isLoading = true, cachedData = null))
        if (shouldLoadFromCache) {
            val dbSource = loadFromCache()
            result.addSource(dbSource) {
                result.removeSource(dbSource)
                setValue(DataState.loading(isLoading = true, cachedData = it))
            }
        }
        if (isNetworkRequest) {
            if (isNetworkAvailable) {
                doNetworkRequest()
            } else {
                if (shouldCancelIfNoInternet) {
                    onErrorReturn(
                        ErrorHandling.UNABLE_TODO_OPERATION_WO_INTERNET,
                        shouldUseDialog = true,
                        shouldUseToast = false
                    )
                } else {
                    doCacheRequest()
                }
            }
        } else {
            doCacheRequest()
        }
    }

    private fun doCacheRequest() {
        coroutineScope.launch {
            // fake delay for testing cache
            delay(Constants.TESTING_CACHE_DELAY)
            // view data from cache only and return
            createCacheRequestAndReturn()
        }
    }

    private fun doNetworkRequest() {
        coroutineScope.launch {
            // simulate network delay for testing
            delay(Constants.TESTING_NETWORK_DELAY)
            withContext(Main) {
                val apiResponse = createCall()
                result.addSource(apiResponse) { respone ->
                    result.removeSource(apiResponse)
                    coroutineScope.launch {
                        handleNetworkCall(respone)
                    }
                }
            }
        }
        GlobalScope.launch(IO) {
            delay(Constants.NETWORK_TIMEOUT)
            if (!job.isCompleted) {
                Log.e(TAG, "NetworkBoundResource: Job Network Time out")
                job.cancel(CancellationException(ErrorHandling.UNABLE_TO_RESOLVE_HOST))
            }
        }
    }

    suspend fun handleNetworkCall(response: GenericApiResponse<ResponseObject>) {
        when (response) {
            is ApiSuccessResponse -> {
                handleApiSuccessResponse(response)
            }
            is ApiErrorResponse -> {
                Log.e(TAG, ":NetworkBoundResource: ${response.errorMessage}")
                onErrorReturn(response.errorMessage, true, false)
            }
            is ApiEmptyResponse -> {
                Log.e(TAG, "NetworkBoundResource: Request returned nothing (http 204)")
                onErrorReturn("HTTP 204, returned nothing", true, false)
            }
        }
    }

    fun onCompleteJob(dataState: DataState<ViewStateType>) {
        GlobalScope.launch(Main) {
            job.complete()
            setValue(dataState)
        }
    }

    private fun setValue(dataState: DataState<ViewStateType>) {
        result.value = dataState
    }

    fun onErrorReturn(errorMessage: String?, shouldUseDialog: Boolean, shouldUseToast: Boolean) {
        var msg = errorMessage
        var useDialog = shouldUseDialog
        var responseType: ResponseType = ResponseType.None()
        if (msg == null) {
            msg = ErrorHandling.ERROR_UNKNOWN
        } else if (ErrorHandling.isNetworkError(msg)) {
            msg = ErrorHandling.ERROR_CHECK_NETWORK_CONNECTION
            useDialog = false
        }
        if (useDialog) {
            responseType = ResponseType.Dialog()
        }
        if (shouldUseToast) {
            responseType = ResponseType.Toast()
        }
        onCompleteJob(
            DataState.error(
                response = Response(
                    message = msg,
                    responseType = responseType
                )
            )
        )
    }

    @UseExperimental(InternalCoroutinesApi::class)
    private fun initNewJob(): Job {
        Log.d(TAG, "initNewJob: CALLED")
        job = Job()
        job.invokeOnCompletion(
            onCancelling = true,
            invokeImmediately = true,
            handler = object : CompletionHandler {
                override fun invoke(cause: Throwable?) {
                    if (job.isCancelled) {
                        Log.e(TAG, "NetworkBoundResource: job has been canceled")
                        cause?.let {
                            onErrorReturn(it.message, false, true)
                        } ?: onErrorReturn(ErrorHandling.ERROR_UNKNOWN, false, true)
                    } else if (job.isCompleted) {
                        Log.d(TAG, "NetworkBoundResource: job is completed")
                        // do nothing, should be handled already
                    }
                }
            })
        coroutineScope = CoroutineScope(IO + job)
        return job
    }

    fun asLiveData() = result as LiveData<DataState<ViewStateType>>
    abstract suspend fun createCacheRequestAndReturn()
    abstract suspend fun handleApiSuccessResponse(response: ApiSuccessResponse<ResponseObject>)
    abstract fun createCall(): LiveData<GenericApiResponse<ResponseObject>>
    abstract fun loadFromCache(): LiveData<ViewStateType>
    abstract suspend fun updateLocalDb(cacheObject: CacheObject?)
    abstract fun setJob(job: Job)
}