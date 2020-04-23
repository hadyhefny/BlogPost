package com.hefny.hady.blogpost.ui.main.account.state

import com.hefny.hady.blogpost.util.StateEvent

sealed class AccountStateEvent : StateEvent {
    class GetAccountPropertiesEvent() : AccountStateEvent() {
        override fun errorInfo(): String {
            return "error retrieving account properties"
        }

        override fun toString(): String {
            return "GetAccountPropertiesEvent"
        }
    }

    data class UpdateAccountPropertiesEvent(
        val email: String,
        val username: String
    ) : AccountStateEvent() {
        override fun errorInfo(): String {
            return "error updating account properties"
        }

        override fun toString(): String {
            return "UpdateAccountPropertiesEvent"
        }
    }

    data class ChangePasswordEvent(
        val currentPassword: String,
        val newPassword: String,
        val confirmNewPassword: String
    ) : AccountStateEvent() {
        override fun errorInfo(): String {
            return "failed to change password"
        }

        override fun toString(): String {
            return "ChangePasswordEvent"
        }
    }

    class None() : AccountStateEvent() {
        override fun errorInfo(): String {
            return "none"
        }

        override fun toString(): String {
            return "None"
        }
    }
}