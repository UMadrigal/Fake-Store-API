package com.macruware.fakestore.ui.login

sealed class LoginState {
    data object Loading: LoginState()
    data class Success(val token: String = "5d1fs5d1f35dgosd8sf4ds08f"): LoginState()
    data class Error(val error: String): LoginState()
}