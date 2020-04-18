package com.hefny.hady.blogpost.ui

import android.app.Activity
import android.widget.Toast
import com.afollestad.materialdialogs.MaterialDialog
import com.hefny.hady.blogpost.R

fun Activity.displayToast(msg: String) {
    Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
}

fun Activity.displayErrorDialog(message: String) {
    MaterialDialog(this).show {
        title(R.string.text_error)
        message(text = message)
        positiveButton(R.string.text_ok)
    }
}

fun Activity.displaySuccessDialog(message: String) {
    MaterialDialog(this).show {
        title(R.string.text_success)
        message(text = message)
        positiveButton(R.string.text_ok)
    }
}

fun Activity.displayInfoDialog(message: String) {
    MaterialDialog(this).show {
        title(R.string.text_info)
        message(text = message)
        positiveButton(R.string.text_ok)
    }
}

fun Activity.areYouSureDialog(
    message: String,
    callback: AreYouSureCallback
) {
    MaterialDialog(this).show {
        title(R.string.are_you_sure_delete)
        message(text = message)
        positiveButton(R.string.text_yes) {
            callback.proceed()
        }
        negativeButton(R.string.text_cancel) {
            callback.cancel()
        }
    }
}

interface AreYouSureCallback {
    fun proceed()
    fun cancel()
}