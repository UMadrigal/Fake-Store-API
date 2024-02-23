package com.macruware.fakestore.ui.home.searchedplp

import com.macruware.fakestore.domain.model.ProductModel

sealed class SearchedApiState {
    data object Loading: SearchedApiState()
    data class Success(val productList: List<ProductModel>): SearchedApiState()
    data class Error(val error: String): SearchedApiState()
}