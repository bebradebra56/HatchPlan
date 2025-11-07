package com.hatchi.planing.soft.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.vector.ImageVector

sealed class BottomNavItem(
    val route: String,
    val icon: ImageVector,
    val label: String
) {
    object Home : BottomNavItem(
        route = Screen.Home.route,
        icon = Icons.Default.Home,
        label = "Home"
    )

    object Batches : BottomNavItem(
        route = Screen.Batches.route,
        icon = Icons.Default.List,
        label = "Batches"
    )

    object Calendar : BottomNavItem(
        route = Screen.Calendar.route,
        icon = Icons.Default.CalendarToday,
        label = "Calendar"
    )

    object Reports : BottomNavItem(
        route = Screen.Reports.route,
        icon = Icons.Default.Assessment,
        label = "Reports"
    )

    object Settings : BottomNavItem(
        route = Screen.Settings.route,
        icon = Icons.Default.Settings,
        label = "Settings"
    )

    companion object {
        val items = listOf(Home, Batches, Calendar, Reports, Settings)
    }
}

