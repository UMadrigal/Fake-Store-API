package com.macruware.fakestore.ui.register

sealed class RegisterState {
    data object Loading: RegisterState()
    data class Success(val newUser: String): RegisterState()
    data class Error(val error: String = "Algo salió mal..."): RegisterState()
}