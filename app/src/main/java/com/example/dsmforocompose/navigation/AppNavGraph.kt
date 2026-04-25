package com.example.dsmforocompose.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.dsmforocompose.data.AppRepository
import com.example.dsmforocompose.ui.screens.GradesScreen
import com.example.dsmforocompose.ui.screens.HistoryScreen
import com.example.dsmforocompose.ui.screens.LoginScreen
import com.example.dsmforocompose.ui.screens.WelcomeScreen

@Composable
fun AppNavGraph(repository: AppRepository) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Screen.Login.route
    ) {
        composable(Screen.Login.route) {
            LoginScreen(
                repository = repository,
                onLoginSuccess = { email ->
                    navController.navigate(Screen.Welcome.createRoute(email)) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                        launchSingleTop = true
                    }
                }
            )
        }

        composable(
            route = Screen.Welcome.route,
            arguments = listOf(navArgument("email") { type = NavType.StringType })
        ) { backStackEntry ->
            val email = backStackEntry.arguments?.getString("email").orEmpty()

            WelcomeScreen(
                email = email,
                onGoToGrades = { navController.navigate(Screen.Grades.createRoute(email)) },
                onGoToHistory = { navController.navigate(Screen.History.createRoute(email)) },
                onLogout = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                        launchSingleTop = true
                    }
                }
            )
        }

        composable(
            route = Screen.Grades.route,
            arguments = listOf(navArgument("email") { type = NavType.StringType })
        ) { backStackEntry ->
            val email = backStackEntry.arguments?.getString("email").orEmpty()

            GradesScreen(
                repository = repository,
                email = email,
                onBackToWelcome = { navController.popBackStack() },
                onLogout = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                        launchSingleTop = true
                    }
                }
            )
        }

        composable(
            route = Screen.History.route,
            arguments = listOf(navArgument("email") { type = NavType.StringType })
        ) { backStackEntry ->
            val email = backStackEntry.arguments?.getString("email").orEmpty()

            HistoryScreen(
                repository = repository,
                email = email,
                onBackToWelcome = { navController.popBackStack() },
                onLogout = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                        launchSingleTop = true
                    }
                }
            )
        }
    }
}