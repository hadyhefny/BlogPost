package com.hefny.hady.blogpost.ui

import com.hefny.hady.blogpost.util.Response
import com.hefny.hady.blogpost.util.StateMessageCallback

interface UICommunicationListener {
    fun onResponseReceived(
        response: Response,
        stateMessageCallback: StateMessageCallback
    )
    fun displayProgressBar(isLoading: Boolean)
}