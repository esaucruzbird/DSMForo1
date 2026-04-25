package com.example.dsmforocompose.navigation

import android.net.Uri

sealed class Screen(val route: String) {

    data object Login : Screen("login")

    data object Welcome : Screen("welcome/{email}") {
        fun createRoute(email: String): String {
            return "welcome/${Uri.encode(email)}"
        }
    }

    data object Grades : Screen("grades/{email}") {
        fun createRoute(email: String): String {
            return "grades/${Uri.encode(email)}"
        }
    }

    data object History : Screen("history/{email}") {
        fun createRoute(email: String): String {
            return "history/${Uri.encode(email)}"
        }
    }
}