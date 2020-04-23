package com.hefny.hady.blogpost.ui.auth.state

import com.hefny.hady.blogpost.util.StateEvent

sealed class AuthStateEvent : StateEvent {
    data class LoginAttemptEvent(val email: String, val password: String) : AuthStateEvent() {
        override fun errorInfo(): String {
            return "failed to login"
        }
    }

    data class RegisterAttemptEvent(
        val email: String,
        val username: String,
        val password: String,
        val confirm_password: String
    ) : AuthStateEvent() {
        override fun errorInfo(): String {
            return "failed to register"
        }
    }

    class CheckPreviousAuthEvent : AuthStateEvent() {
        override fun errorInfo(): String {
            return "error checking for previous authenticated user"
        }
    }

    class None : AuthStateEvent() {
        override fun errorInfo(): String {
            return "none"
        }
    }
}