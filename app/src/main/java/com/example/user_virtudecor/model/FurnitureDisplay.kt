package com.example.user_virtudecor.model
import android.os.Parcelable
import kotlinx.parcelize.Parcelize
@Parcelize
data class FurnitureDisplay(
    val id: String = "",
    val name: String = "",
    val price: String = "",
    var category: String = "",
    val imageUrls: List<String> = emptyList(),
    val description: String = "",
    val glbModelUrl: String=""
// add this if missing
): Parcelable

