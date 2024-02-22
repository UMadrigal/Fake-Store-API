package com.macruware.fakestore.ui.main

import com.macruware.fakestore.domain.model.CategoryNameModel

sealed class MainApiState {
    data object Loading : MainApiState()
    data class Success(val categoryNameList: List<CategoryNameModel>) : MainApiState()
    data class Error(val error: String = "Error al conectar con el servidor."): MainApiState()
}