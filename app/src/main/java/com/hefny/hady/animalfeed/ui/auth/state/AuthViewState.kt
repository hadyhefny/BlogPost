package com.hefny.hady.animalfeed.ui.auth.state

import com.hefny.hady.animalfeed.models.AuthToken

class AuthViewState(
    var registrationFields: RegistrationFields? = RegistrationFields(),
    var loginFields: LoginFields? = LoginFields(),
    var authToken: AuthToken? = null
)

data class RegistrationFields(
    var register_email: String? = null,
    var register_username: String? = null,
    var register_password: String? = null,
    var register_confirm_password: String? = null
) {
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

data class LoginFields(
    var login_email: String? = null,
    var login_password: String? = null
) {
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