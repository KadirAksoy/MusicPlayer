package com.kadiraksoy.kadir

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController


@Composable
fun AppNavigation(navController: NavHostController) {
    val navController = rememberNavController()


    NavHost(
        navController = navController,
        startDestination = "musicPlayerScreen"
    ) {
        composable("musicPlayerScreen") {
            MusicPlayerScreen(navController = navController)
        }
        composable("favoriteList") {
            FavoriteList(navController = navController)
        }




    }
}