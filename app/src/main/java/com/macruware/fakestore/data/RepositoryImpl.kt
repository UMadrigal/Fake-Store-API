package com.macruware.fakestore.data

import android.util.Log
import com.macruware.fakestore.data.network.FakeStoreApiService
import com.macruware.fakestore.domain.Repository
import com.macruware.fakestore.domain.model.CategoryNameModel
import com.macruware.fakestore.domain.model.CategoryProductModel
import javax.inject.Inject

class RepositoryImpl @Inject constructor(private val apiService: FakeStoreApiService): Repository {

    override suspend fun getAllCategories(): List<CategoryNameModel>? {
        runCatching { apiService.getAllCategories() }
            .onSuccess { return it.toDomain() }
            .onFailure { Log.e("ApiState", "Ha ocurrido un error en la llamada: ${it.message}") }
        return null
    }

    override suspend fun getProductsInCategory(category: String): CategoryProductModel? {
        runCatching { apiService.getProductsInCategory(category) }
            .onSuccess { return it.toDomain() }
            .onFailure { Log.e("ApiState", "Ha ocurrido un error en la llamada: ${it.message}") }
        return null
    }
}