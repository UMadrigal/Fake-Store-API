package com.macruware.fakestore.domain.model

data class ReviewModel(val personName: String,
                       val profilePicture: String,
                       val score: Double,
                       val reviewText: String)