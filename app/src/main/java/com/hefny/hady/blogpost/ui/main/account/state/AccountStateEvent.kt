package com.hefny.hady.blogpost.ui.main.account.state

sealed class AccountStateEvent {
    class GetAccountPropertiesEvent() : AccountStateEvent()
    data class UpdateAccountPropertiesEvent(
        val email: String,
        val userName: String
    ) : AccountStateEvent()

    data class ChangePasswordEvent(
        val currentPassword: String,
        val newPassword: String,
        val confirmNewPassword: String
    ) : AccountStateEvent()

    class None() : AccountStateEvent()
}