package com.example.user_virtudecor.navigation

import ARScreen
import ProductDetailScreen
import android.net.Uri
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.user_virtudecor.screen.CategoryScreen
import com.example.user_virtudecor.screen.HomeScreen
import com.example.user_virtudecor.screen.LoginScreen

import com.example.user_virtudecor.screen.SeeAllScreen
import com.example.user_virtudecor.screen.ShoppingScreen
import com.example.user_virtudecor.screen.SignupScreen

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun User_navigation() {
    var navController = rememberNavController()

    NavHost(navController = navController, startDestination = "login") {

        composable("category_screen/{categoryTitle}") { backStackEntry ->
            val categoryTitle = backStackEntry.arguments?.getString("categoryTitle") ?: ""
            CategoryScreen(
                categoryTitle = categoryTitle,
                navController = navController
            )
        }
        composable("shoppingscreen") { ShoppingScreen(navController) }
        composable("login") { LoginScreen(navController) }
        composable("signup") { SignupScreen(navController) }
        composable("home") { HomeScreen(navController) }

        composable("seeAll") { SeeAllScreen(navController) }
        composable(
            route = "product_detail/{itemJson}",
            arguments = listOf(navArgument("itemJson") { type = NavType.StringType })
        ) { backStackEntry ->
            val itemJson = backStackEntry.arguments?.getString("itemJson") ?: ""
            ProductDetailScreen(navController, itemJson)
        }
        composable("arscreen/{modelUrl}") { backStackEntry ->
            val encodedUrl = backStackEntry.arguments?.getString("modelUrl") ?: ""

            val url = Uri.decode(encodedUrl)

            val prefix = "furniture_models/"
            val startIndex = url.indexOf(prefix)

            val fromIndex = startIndex + prefix.length
            val endIndex = url.indexOf("?", fromIndex).takeIf { it != -1 } ?: url.length
            val result = url.substring(fromIndex, endIndex)
            // Output: db60e1ee-6fb7-490e-b1f5-befd634343ec


            ARScreen(

                modelId = result
            ) {
                navController.popBackStack()
            }
        }


    }

}



