package com.macruware.fakestore.domain.usecase

import com.macruware.fakestore.domain.Repository
import com.macruware.fakestore.domain.model.CategoryProductModel
import javax.inject.Inject

class GetProductsInCategoryUseCase @Inject constructor(private val repository: Repository) {

    suspend fun invoke(category: String): CategoryProductModel? = repository.getProductsInCategory(category)
}