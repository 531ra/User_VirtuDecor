package com.example.user_virtudecor.model

import com.example.user_virtudecor.R
import com.example.user_virtudecor.ui.theme.CategoryOne
import com.example.user_virtudecor.ui.theme.CategoryTwo



import androidx.annotation.DrawableRes
import androidx.compose.ui.graphics.Color



data class Category(
    val id: Int,
    val title: String,
    @DrawableRes val image: Int,
    val color: Color
)

val categoryList = listOf(
    Category(
        1,
        "Chair",
        R.drawable.chair,
        CategoryOne
    ),
    Category(
        2,
        "Sofa",
        R.drawable.sofa,
        CategoryTwo
    ),
    Category(
        3,
        "HomeDecor",
        R.drawable.desk,
        CategoryOne
    ),
    Category(
        4,
        "Bed",
        R.drawable.bed,
        CategoryTwo
    )
)