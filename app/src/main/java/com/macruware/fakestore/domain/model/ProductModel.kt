package com.macruware.fakestore.domain.model

data class ProductModel(val name: String,
                        val price: Double,
                        val description: String,
                        val image: String,
                        val rate: Double,
                        val count: Int)
