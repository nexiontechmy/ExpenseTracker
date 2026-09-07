package com.expensetracker.app.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.automirrored.filled.ShowChart
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(val route: String, val label: String, val icon: ImageVector) {
    object Dashboard : Screen("dashboard", "Home", Icons.Filled.Home)
    object Transactions : Screen("transactions", "History", Icons.AutoMirrored.Filled.List)
    object Trends : Screen("trends", "Trends", Icons.AutoMirrored.Filled.ShowChart)
    object Settings : Screen("settings", "Settings", Icons.Filled.Settings)

    companion object {
        val bottomNavItems = listOf(Dashboard, Transactions, Trends, Settings)
    }
}
