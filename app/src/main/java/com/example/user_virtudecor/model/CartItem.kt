package com.example.user_virtudecor.model


data class CartItem(
    val id: String = "",
    val name: String = "",
    val price: String = "",
    val imageUrl: String = "",
    val quantity: Int = 1
)