package com.macruware.fakestore.data.network.response


import com.google.gson.annotations.SerializedName

data class ProductInCategoryResponseItem(
    @SerializedName("category")
    val category: String,
    @SerializedName("description")
    val description: String,
    @SerializedName("image")
    val image: String,
    @SerializedName("price")
    val price: String,
    @SerializedName("rating")
    val rating: Rating,
    @SerializedName("title")
    val title: String
)