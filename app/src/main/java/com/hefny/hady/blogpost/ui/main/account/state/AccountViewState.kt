package com.hefny.hady.blogpost.ui.main.account.state

import android.os.Parcelable
import com.hefny.hady.blogpost.models.AccountProperties
import kotlinx.android.parcel.Parcelize

const val ACCOUNT_VIEW_STATE_BUNDLE_KEY = "com.hefny.hady.blogpost.ui.main.account.state.AccountViewState"

@Parcelize
class AccountViewState(
    var accountProperties: AccountProperties? = null
) : Parcelable