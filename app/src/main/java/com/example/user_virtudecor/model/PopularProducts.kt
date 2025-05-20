package com.example.user_virtudecor.model



import androidx.annotation.DrawableRes
import com.example.user_virtudecor.R


data class PopularProducts(
    val id: Int,
    val title: String,
    @DrawableRes val image: Int,
    val price: String,
    val fileName: String
)

val popularProductList = listOf<PopularProducts>(
    PopularProducts(
        1,
        "Sverom chair",
        R.drawable.product_one,
        "$400","chair.glb"
    ),
    PopularProducts(
        2,
        "Norrviken chair and table",
        R.drawable.product_two,
        "$999","decorative_furniture.glb"
    ),
    PopularProducts(
        3,
        "Ektorp sofa",
        R.drawable.product_three,
        "$800","sofa.glb"
    ),
    PopularProducts(
        4,
        "Jan Sflanaganvik sofa",
        R.drawable.product_four,
        "$700", "storage_cabinet_furniture.glb"
    )
)