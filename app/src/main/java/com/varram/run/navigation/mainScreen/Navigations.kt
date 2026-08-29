package com.varram.run.navigation.mainScreen

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.ListAlt
import androidx.compose.material.icons.filled.Timeline
import androidx.compose.ui.graphics.vector.ImageVector

sealed class BottomNavItem(
    val route: String,
    val title: String,
    val icon: ImageVector
) {
    object Home : BottomNavItem(
        route = ScreenRoute.Home.route,
        title = "Home",
        icon = Icons.Default.Home
    )

    object History : BottomNavItem(
        route = ScreenRoute.History.route,
        title = "Tasks",
        icon = Icons.Default.History
    )

    companion object {
        val items = listOf(Home, History)
    }
}


