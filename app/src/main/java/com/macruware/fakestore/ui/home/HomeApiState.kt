package com.macruware.fakestore.ui.home

import com.macruware.fakestore.domain.model.CategoryProductModel
import com.macruware.fakestore.domain.model.ProductModel

sealed class HomeApiState {
    data object Loading: HomeApiState()
    data class Success(val categoryWithProductList: List<CategoryProductModel>): HomeApiState()
    data class Error(val error: String): HomeApiState()
}