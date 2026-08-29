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

sealed class ScreenRoute(
    val route: String,
    val title: String,
    val icon: ImageVector
) {
    object Home : ScreenRoute("home", "Home", Icons.Default.Home)
    object History : ScreenRoute("history", "History", Icons.Default.History)
    object AddTask : ScreenRoute("add?responsibilityId={responsibilityId}", "Add Tasks", Icons.Default.Add) {
        fun createRoute(id: Long? = null) = if (id != null) "add?responsibilityId=$id" else "add"
    }

    // Non-bottom nav screens can be declared here without icons
    object Detail : ScreenRoute("detail/{responsibilityId}", "Detail", Icons.Default.ListAlt) {
        fun createRoute(id: Long) = "detail/$id"
    }

    companion object {
        val bottomNavItems = listOf(
            Home,
            History
        )
    }
}