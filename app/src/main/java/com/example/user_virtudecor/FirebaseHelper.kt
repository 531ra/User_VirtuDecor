package com.example.user_virtudecor

import com.example.user_virtudecor.model.FurnitureDisplay
import com.google.firebase.database.FirebaseDatabase

object FirebaseHelper {

    fun fetchAllFurnitureItems(callback: (List<FurnitureDisplay>) -> Unit) {
        val furnitureList = mutableListOf<FurnitureDisplay>()
        val furnitureRef = FirebaseDatabase.getInstance().getReference("furniture")

        furnitureRef.get().addOnSuccessListener { categoriesSnapshot ->
            for (categorySnap in categoriesSnapshot.children) {
                val categoryName = categorySnap.key ?: continue

                for (itemSnap in categorySnap.children) {
                    val id = itemSnap.key ?: ""  // Use the Firebase key as id
                    val name = itemSnap.child("name").getValue(String::class.java) ?: ""
                    val priceString = itemSnap.child("price").getValue(String::class.java) ?: "0.0"
                    val priceDouble = priceString.toDoubleOrNull() ?: 0.0
                    val description=itemSnap.child("description").getValue(String::class.java) ?: ""
                    val imagesList = mutableListOf<String>()
                    val glbModelUrl= itemSnap.child("glbModelUrl").getValue(String::class.java) ?: ""
                    val imagesSnapshot = itemSnap.child("images")  // Use "images" key to match admin code
                    if (imagesSnapshot.exists()) {
                        for (imageSnap in imagesSnapshot.children) {
                            imageSnap.getValue(String::class.java)?.let { url ->
                                imagesList.add(url)
                            }
                        }
                    }

                    furnitureList.add(
                        FurnitureDisplay(
                            id = id,
                            name = name,
                            price = priceDouble.toString(),
                            category = categoryName,
                            imageUrls = imagesList,
                            description = description,
                            glbModelUrl = glbModelUrl,
                        )
                    )
                }
            }
            callback(furnitureList)
        }.addOnFailureListener {
            callback(emptyList())
        }
    }

    fun fetchFurnitureByCategory(
        category: String,
        onResult: (List<FurnitureDisplay>) -> Unit
    ) {
        val databaseRef = FirebaseDatabase.getInstance().getReference("furniture").child(category)

        databaseRef.get().addOnSuccessListener { snapshot ->
            val list = mutableListOf<FurnitureDisplay>()
            for (itemSnapshot in snapshot.children) {
                // Manually parsing fields to avoid null and ensure proper initialization:
                val id = itemSnapshot.key ?: ""
                val name = itemSnapshot.child("name").getValue(String::class.java) ?: ""
                val priceString = itemSnapshot.child("price").getValue(String::class.java) ?: "0.0"
                val priceDouble = priceString.toDoubleOrNull() ?: 0.0
                val description = itemSnapshot.child("description").getValue(String::class.java) ?: ""
                val glbModelUrl = itemSnapshot.child("glbModelUrl").getValue(String::class.java) ?: ""

                val imagesList = mutableListOf<String>()
                val imagesSnapshot = itemSnapshot.child("images")
                if (imagesSnapshot.exists()) {
                    for (imageSnap in imagesSnapshot.children) {
                        imageSnap.getValue(String::class.java)?.let { url ->
                            imagesList.add(url)
                        }
                    }
                }

                list.add(
                    FurnitureDisplay(
                        id = id,
                        name = name,
                        price = priceDouble.toString(),
                        category = category,
                        imageUrls = imagesList,
                        description = description,
                        glbModelUrl = glbModelUrl
                    )
                )
            }
            onResult(list)
        }.addOnFailureListener {
            onResult(emptyList())
        }}}

