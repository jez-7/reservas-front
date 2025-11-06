package com.turnos.navigation

sealed class Screen(val route: String) {
    object Welcome : Screen("welcome")
    object Login : Screen("login")
    object Register : Screen("register")
    object Home : Screen("home")
    object  Services : Screen("services")

    object Stats : Screen("stats")

    object Profile : Screen("profile")


}