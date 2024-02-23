package com.macruware.fakestore.domain.usecase

import com.macruware.fakestore.domain.Repository
import com.macruware.fakestore.domain.model.CategoryNameModel
import javax.inject.Inject

class GetAllCategoriesUseCase @Inject constructor(private val repository: Repository) {

    suspend fun invoke(): List<CategoryNameModel>? = repository.getAllCategories()
}