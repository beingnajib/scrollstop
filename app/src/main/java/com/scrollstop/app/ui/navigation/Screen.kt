package com.scrollstop.app.ui.navigation

sealed class Screen(val route: String) {
    data object Onboarding : Screen("onboarding")
    data object Home : Screen("home")
    data object Settings : Screen("settings")
    data object Stats : Screen("stats")
}
