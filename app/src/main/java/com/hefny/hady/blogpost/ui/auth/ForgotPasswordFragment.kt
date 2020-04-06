package com.hefny.hady.blogpost.ui.auth


import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.TranslateAnimation
import android.webkit.JavascriptInterface
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.navigation.fragment.findNavController
import com.hefny.hady.blogpost.R
import com.hefny.hady.blogpost.ui.DataState
import com.hefny.hady.blogpost.ui.DataStateChangeListener
import com.hefny.hady.blogpost.ui.Response
import com.hefny.hady.blogpost.ui.ResponseType
import com.hefny.hady.blogpost.util.Constants
import kotlinx.android.synthetic.main.fragment_forgot_password.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch

class ForgotPasswordFragment : BaseAuthFragment() {
    private val TAG: String = "AppDebug"
    lateinit var webView: WebView
    lateinit var stateChangeListener: DataStateChangeListener
    val webInteractionCallback = object : WebAppInterface.OnWebInteractionCallBack {
        override fun onSuccess(email: String) {
            Log.d(TAG, "onSuccess: a reset link will be sent to $email")
            onPasswordResetLinkSent()
        }

        override fun onError(errorMessage: String) {
            Log.e(TAG, "onError: $errorMessage")
            val dataState = DataState.error<Any>(Response(errorMessage, ResponseType.Dialog()))
            stateChangeListener.onDataStateChange(dataState)
        }

        override fun onLoading(isLoading: Boolean) {
            Log.d(TAG, "onLoading: $isLoading")
            CoroutineScope(Main).launch {
                stateChangeListener.onDataStateChange(DataState.loading<Any>(isLoading))
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_forgot_password, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        webView = view.findViewById(R.id.webview)
        loadPasswordResetWebView()
        return_to_launcher_fragment.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    @SuppressLint("setJavaScriptEnabled")
    private fun loadPasswordResetWebView() {
        stateChangeListener.onDataStateChange(
            DataState.loading<Any>(true)
        )
        webView.webViewClient = object : WebViewClient() {
            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                stateChangeListener.onDataStateChange(
                    DataState.loading<Any>(false)
                )
            }
        }
        webView.loadUrl(Constants.PASSWORD_RESET_URL)
        webView.settings.javaScriptEnabled = true
        webView.addJavascriptInterface(
            WebAppInterface(webInteractionCallback),
            "AndroidTextListener"
        )
    }

    class WebAppInterface(private val callback: OnWebInteractionCallBack) {

        @JavascriptInterface
        fun onSuccess(email: String) {
            callback.onSuccess(email)
        }

        @JavascriptInterface
        fun onError(errorMessage: String) {
            callback.onError(errorMessage)
        }

        @JavascriptInterface
        fun onLoading(isLoading: Boolean) {
            callback.onLoading(isLoading)
        }

        interface OnWebInteractionCallBack {
            fun onSuccess(email: String)
            fun onError(errorMessage: String)
            fun onLoading(isLoading: Boolean)
        }
    }

    private fun onPasswordResetLinkSent() {
        CoroutineScope(Main).launch {
            parent_view.removeView(webview)
            webview.destroy()
            val animation = TranslateAnimation(
                password_reset_done_container.width.toFloat(),
                0f,
                0f,
                0f
            )
            animation.duration = 500
            password_reset_done_container.startAnimation(animation)
            password_reset_done_container.visibility = View.VISIBLE
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            stateChangeListener = context as DataStateChangeListener
        } catch (e: ClassCastException) {
            Log.d(TAG, "onAttach: $context must implement DataStateChangeListener")
        }
    }
}
