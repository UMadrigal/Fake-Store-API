package com.macruware.fakestore.domain.model

data class ProductModel(val name: String,
                        val price: String,
                        val category: String,
                        val description: String,
                        val image: String,
                        val rate: Double,
                        val count: Int,
                        val reviewList: List<ReviewModel> = listOf(
                            ReviewModel("Ana García",
                                "https://st3.depositphotos.com/1007566/32958/v/450/depositphotos_329584890-stock-illustration-young-man-avatar-character-icon.jpg",
                                4.5, "¡Excelente prenda, muy cómoda y de alta calidad!"),
                            ReviewModel("Carlos Rodríguez",
                                "https://st3.depositphotos.com/1007566/32958/v/450/depositphotos_329584890-stock-illustration-young-man-avatar-character-icon.jpg",
                                3.0, "Buen producto, pero esperaba un poco más."),
                            ReviewModel("María López",
                                "https://st3.depositphotos.com/1007566/32958/v/450/depositphotos_329584890-stock-illustration-young-man-avatar-character-icon.jpg",
                                5.0, "Increíble, la mejor compra que he hecho."),
                            ReviewModel("Juan Pérez",
                                "https://st3.depositphotos.com/1007566/32958/v/450/depositphotos_329584890-stock-illustration-young-man-avatar-character-icon.jpg",
                                2.5, "No cumplió con mis expectativas."),
                            ReviewModel("Laura Martínez",
                                "https://st3.depositphotos.com/1007566/32958/v/450/depositphotos_329584890-stock-illustration-young-man-avatar-character-icon.jpg",
                                4.0, "Buena relación calidad-precio.")
                        )
)
