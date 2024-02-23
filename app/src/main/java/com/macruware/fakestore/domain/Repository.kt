package com.macruware.fakestore.domain

import com.macruware.fakestore.domain.model.CategoryNameModel
import com.macruware.fakestore.domain.model.CategoryProductModel
import com.macruware.fakestore.domain.model.ProductModel

interface Repository {

    suspend fun getAllCategories(): List<CategoryNameModel>?

    suspend fun getProductsInCategory(category: String): CategoryProductModel?

    suspend fun getAllProducts(): List<ProductModel>?
}