package com.macruware.fakestore.ui.login

sealed class LoginState {
    data object Loading: LoginState()
    data class Success(val token: String = "eyJhbGciOiJIUzI1NiIsInR"): LoginState()
    data class Error(val error: String): LoginState()
}