package com.macruware.fakestore.domain.usecase

import com.macruware.fakestore.domain.Repository
import com.macruware.fakestore.domain.model.ProductModel
import javax.inject.Inject

class GetAllProductsUseCase @Inject constructor(private val repository: Repository) {
    suspend fun invoke(): List<ProductModel>? = repository.getAllProducts()
}