package com.hefny.hady.blogpost.ui.auth.state

import android.os.Parcelable
import com.hefny.hady.blogpost.models.AuthToken
import kotlinx.android.parcel.Parcelize

const val AUTH_VIEW_STATE_BUNDLE_KEY = "com.hefny.hady.blogpost.ui.auth.state.AuthViewState"

@Parcelize
class AuthViewState(
    var registrationFields: RegistrationFields? = RegistrationFields(),
    var loginFields: LoginFields? = LoginFields(),
    var authToken: AuthToken? = null
) : Parcelable

@Parcelize
data class RegistrationFields(
    var register_email: String? = null,
    var register_username: String? = null,
    var register_password: String? = null,
    var register_confirm_password: String? = null
) : Parcelable {
    class RegistrationError {
        companion object {
            fun mustFillAllFields(): String {
                return "All fields are required"
            }

            fun passwordsDoNotMatch(): String {
                return "Password doesn't match"
            }

            fun none(): String {
                return "none"
            }
        }
    }

    fun isValidForRegistration(): String {
        if (register_email.isNullOrEmpty()
            || register_username.isNullOrEmpty()
            || register_password.isNullOrEmpty()
            || register_confirm_password.isNullOrEmpty()
        ) {
            return RegistrationError.mustFillAllFields()
        }
        if (!register_password.equals(register_confirm_password)) {
            return RegistrationError.passwordsDoNotMatch()
        }
        return RegistrationError.none()
    }
}

@Parcelize
data class LoginFields(
    var login_email: String? = null,
    var login_password: String? = null
) : Parcelable {
    class LoginError {
        companion object {
            fun mustFillAllFields(): String {
                return "You can't login without email and password"
            }

            fun none(): String {
                return "None"
            }
        }
    }

    fun isValidForLogin(): String {
        if (login_email.isNullOrEmpty()
            || login_password.isNullOrEmpty()
        ) {
            return LoginError.mustFillAllFields()
        }
        return LoginError.none()
    }
}