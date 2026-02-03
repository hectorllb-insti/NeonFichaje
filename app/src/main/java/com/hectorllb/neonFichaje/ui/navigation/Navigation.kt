package com.hectorllb.neonFichaje.ui.navigation

sealed class Screen(val route: String) {
    object Home : Screen("home")
    object Schedule : Screen("schedule")
    object Stats : Screen("stats")
    object Settings : Screen("settings")
    object History : Screen("history")
}
