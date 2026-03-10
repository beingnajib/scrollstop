package com.scrollstop.app.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.scrollstop.app.data.PreferencesManager
import com.scrollstop.app.security.PinManager
import com.scrollstop.app.ui.home.HomeScreen
import com.scrollstop.app.ui.onboarding.OnboardingScreen
import com.scrollstop.app.ui.settings.SettingsScreen
import com.scrollstop.app.ui.stats.StatsScreen

@Composable
fun ScrollStopNavHost(
    preferencesManager: PreferencesManager,
    pinManager: PinManager
) {
    val navController = rememberNavController()
    val onboardingCompleted by preferencesManager.onboardingCompleted.collectAsState(initial = null)

    val startDestination = when (onboardingCompleted) {
        true -> Screen.Home.route
        false -> Screen.Onboarding.route
        null -> return
    }

    NavHost(navController = navController, startDestination = startDestination) {
        composable(Screen.Onboarding.route) {
            OnboardingScreen(
                preferencesManager = preferencesManager,
                pinManager = pinManager,
                onComplete = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Onboarding.route) { inclusive = true }
                    }
                }
            )
        }
        composable(Screen.Home.route) {
            HomeScreen(
                preferencesManager = preferencesManager,
                pinManager = pinManager,
                onNavigateToSettings = { navController.navigate(Screen.Settings.route) },
                onNavigateToStats = { navController.navigate(Screen.Stats.route) }
            )
        }
        composable(Screen.Settings.route) {
            SettingsScreen(
                preferencesManager = preferencesManager,
                pinManager = pinManager,
                onBack = { navController.popBackStack() }
            )
        }
        composable(Screen.Stats.route) {
            StatsScreen(onBack = { navController.popBackStack() })
        }
    }
}
