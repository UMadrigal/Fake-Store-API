package com.macruware.fakestore.data.network.response

import com.macruware.fakestore.domain.model.CategoryNameModel

class CategoriesResponse : ArrayList<String>(){

    fun toDomain(): List<CategoryNameModel>{
        val listToDomain = mutableListOf<CategoryNameModel>()

        for (category in this){
            val categoryNameModel = CategoryNameModel(category)
            listToDomain.add(categoryNameModel)
        }

        return listToDomain
    }
}