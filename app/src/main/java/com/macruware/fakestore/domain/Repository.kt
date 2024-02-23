package com.macruware.fakestore.domain

import com.macruware.fakestore.domain.model.CategoryNameModel
import com.macruware.fakestore.domain.model.CategoryProductModel

interface Repository {

    suspend fun getAllCategories(): List<CategoryNameModel>?

    suspend fun getProductsInCategory(category: String): CategoryProductModel?
}