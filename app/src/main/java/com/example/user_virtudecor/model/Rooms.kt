package com.example.user_virtudecor.model

import androidx.annotation.DrawableRes
import com.example.user_virtudecor.R


data class Rooms(
    val id:Int,
    @DrawableRes val image:Int,
    val title:String
)

val roomList = listOf<Rooms>(
    Rooms(
        1,
        R.drawable.dinning_room,
        "Dining Room"
    ),
    Rooms(
        2,
        R.drawable.bed_room,
        "Bed Room"
    ),
    Rooms(
        3,
        R.drawable.dinning_room,
        "Office Room"
    ),
    Rooms(
        4,
        R.drawable.bed_room,
        "Study Room"
    )
)