package com.pemmob.responsikotlinshiftg.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.pemmob.responsikotlinshiftg.ui.detail.DetailScreen
import com.pemmob.responsikotlinshiftg.ui.home.HomeScreen

sealed class Screen(val route: String) {
    data object Home : Screen("home")
    data object Detail : Screen("detail/{animeId}") {
        const val ARG_ANIME_ID = "animeId"
        fun createRoute(animeId: Int) = "detail/$animeId"
    }
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = Screen.Home.route) {
        composable(Screen.Home.route) {
            HomeScreen(
                onAnimeClick = { animeId ->
                    navController.navigate(Screen.Detail.createRoute(animeId))
                }
            )
        }
        composable(
            route = Screen.Detail.route,
            arguments = listOf(navArgument(Screen.Detail.ARG_ANIME_ID) { type = NavType.IntType })
        ) {
            DetailScreen(onBack = { navController.popBackStack() })
        }
    }
}
