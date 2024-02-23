package com.macruware.fakestore.data.network

import com.macruware.fakestore.data.network.response.CategoriesResponse
import com.macruware.fakestore.data.network.response.ProductInCategoryResponse
import retrofit2.http.GET
import retrofit2.http.Path

interface FakeStoreApiService {

    @GET("/products/categories")
    suspend fun getAllCategories(): CategoriesResponse

    @GET("/products/category/{category}")
    suspend fun getProductsInCategory(@Path("category") category: String): ProductInCategoryResponse

}