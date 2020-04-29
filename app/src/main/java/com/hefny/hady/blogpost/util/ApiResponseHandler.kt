package com.hefny.hady.blogpost.util

import android.util.Log
import com.hefny.hady.blogpost.api.GenericResponse
import com.hefny.hady.blogpost.util.ErrorHandling.Companion.ERROR_CHECK_NETWORK_CONNECTION
import com.hefny.hady.blogpost.util.ErrorHandling.Companion.NETWORK_ERROR
import org.json.JSONException
import org.json.JSONObject

abstract class ApiResponseHandler<ViewState, Data>(
    private val response: ApiResult<Data?>,
    private val stateEvent: StateEvent
) {
    private val TAG = "AppDebug"
    suspend fun getResult(): DataState<ViewState> {
        return when (response) {
            is ApiResult.GenericError -> {
                Log.d(TAG, "getResult0: ${response.errorMessage}")
                DataState.error(
                    response = Response(
                        message = "${stateEvent.errorInfo()}\n\nReason: ${parseErrorMsg(response.errorMessage)}",
                        uiComponentType = UIComponentType.Dialog(),
                        messageType = MessageType.Error()
                    ),
                    stateEvent = stateEvent
                )
            }
            is ApiResult.NetworkError -> {
                Log.d(TAG, "getResult: ${response}")
                DataState.error(
                    response = Response(
                        message = "${stateEvent.errorInfo()}\n\nReason: ${NETWORK_ERROR}",
                        uiComponentType = UIComponentType.Dialog(),
                        messageType = MessageType.Error()
                    ),
                    stateEvent = stateEvent
                )
            }
            is ApiResult.Success -> {
                Log.d(TAG, "getResult success: $response")
                if (response.value == null) {
                    DataState.error(
                        response = Response(
                            message = "${stateEvent.errorInfo()}\n\nReason: Data is NULL.",
                            uiComponentType = UIComponentType.Dialog(),
                            messageType = MessageType.Error()
                        ),
                        stateEvent = stateEvent
                    )
                } else {
                    handleSuccess(resultObj = response.value)
                }
            }
        }
    }

    abstract suspend fun handleSuccess(resultObj: Data): DataState<ViewState>
    private fun parseErrorMsg(rawJson: String?): String {
        try {
            if (!rawJson.isNullOrBlank()) {
                if (rawJson.contains(ERROR_CHECK_NETWORK_CONNECTION)) {
                    return NETWORK_ERROR
                }
                return JSONObject(rawJson).get("response") as String
            }
        } catch (e: JSONException) {
            Log.e(TAG, "parseErrorMsg: ", e)
        }
        return ""
    }
}