package com.example.user_virtudecor.model

data class Furniture(
    val id: String = "",
    val name: String = "",
    val price: String = "",
    val description: String = "",
    val images: List<String> = emptyList(), // names in storage
    val glbModelUrl: String = "",
    val category: String = ""
)
