package com.varram.run.navigation.mainScreen

import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.core.graphics.toColorInt
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState

@Composable
fun AppNavigationBar(navController: NavController) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    fun String.toComposeColor(): Color =
        Color(this.toColorInt())
    NavigationBar (
        modifier = Modifier.shadow(elevation = 4.dp),
        containerColor = "#fbf9f8".toComposeColor(),
        tonalElevation = 8.dp) {
        BottomNavItem.items.forEach { item ->
            val selected = currentRoute == item.route

            NavigationBarItem(
                selected = selected,
                onClick = {
                    if (currentRoute != item.route) {
                        navController.navigate(item.route) {
                            // Pop up to the start destination to avoid building up a large stack
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            // Avoid multiple copies of the same destination when reselecting
                            launchSingleTop = true
                            // Restore state when reselecting a previously selected item
                            restoreState = true
                        }
                    }
                },
                icon = {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = item.title,
                        // Custom icon tint handling
                        tint = if (selected) "#00458F".toComposeColor() else "#424752".toComposeColor()
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    indicatorColor = Color.Transparent,                 // Disables the background glow/pill
                    selectedIconColor = "#00458F".toComposeColor(),             // Selected icon color
                    unselectedIconColor = "#424752".toComposeColor(),  // Unselected icon color
                    selectedTextColor = "#00458F".toComposeColor(),             // Selected text color
                    unselectedTextColor = "#424752".toComposeColor()   // Unselected text color
                ),
                label = { Text(item.title, color = if (selected) "#00458F".toComposeColor() else "#424752".toComposeColor()) }
            )
        }

    }
}