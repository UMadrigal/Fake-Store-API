package com.macruware.fakestore.ui.main

sealed class MainUiState{
    data object HomeProductList: MainUiState()
    data object HomeCategoryPlp: MainUiState()
    data object HomeSearchedProduct: MainUiState()
    data object HomeProductDetail: MainUiState()
}
