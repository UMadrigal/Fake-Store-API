package com.macruware.fakestore.data.network.response

import com.macruware.fakestore.domain.model.ProductModel

class ProductListResponse: ArrayList<ProductInCategoryResponseItem>(){

    fun toDomain(): List<ProductModel> {
        val listToDomain = mutableListOf<ProductModel>()

        // Mapeando un ProductModel a partir de un ProductInCategoryResponseItem
        for (product in this) {
            val productModel = ProductModel(
                name = product.title,
                price = product.price,
                category = product.category,
                description = product.description,
                image = product.image,
                rate = product.rating.rate,
                count = product.rating.count
            )

            listToDomain.add(productModel)
        }

        return listToDomain
    }
}