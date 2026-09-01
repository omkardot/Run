package com.varram.run.navigation.mainScreen
import androidx.navigation.NavType
import androidx.navigation.navArgument
import android.content.Context
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.varram.run.RunningTrackerApplication
import com.varram.run.feature.details.presentation.RunDetailsScreen
import com.varram.run.feature.details.presentation.RunDetailsViewModel
import com.varram.run.feature.details.presentation.RunDetailsViewModelFactory
import com.varram.run.feature.history.presentation.HistoryScreen
import com.varram.run.feature.history.presentation.HistoryViewModel
import com.varram.run.feature.history.presentation.HistoryViewModelFactory
import com.varram.run.feature.home.presentation.RunningTrackerDebugScreen
import com.varram.run.feature.home.presentation.RunningTrackerViewModel
@Composable
fun MainScreen(
    viewModel: RunningTrackerViewModel,context: Context
) {
    val navController = rememberNavController()

    val navBackStackEntry by
    navController.currentBackStackEntryAsState()

    val currentRoute =
        navBackStackEntry?.destination?.route

    val showBottomBar =
        currentRoute in BottomNavItem.items.map { it.route }

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                AppNavigationBar(
                    navController = navController
                )
            }
        }
    ) { innerPadding ->

        NavHost(
            navController = navController,
            startDestination = BottomNavItem.Home.route,
            modifier = Modifier.padding(innerPadding)
        ) {

            composable(BottomNavItem.Home.route) {

                RunningTrackerDebugScreen(
                    latitude = uiState.latitude,
                    longitude = uiState.longitude,
                    accuracy = uiState.accuracy,
                    isTracking = uiState.isTracking,
                    routePoints = uiState.routePoints,
                    distance = uiState.distanceMeters,
                    paceperSec = uiState.paceSecondsPerKm,
                    onToggleTracking = {

                        if (!uiState.isTracking) {
                            viewModel.startTracking(context)
                        } else {
                            viewModel.stopTracking(context)
                        }
                    }
                )
            }
            composable(
                route = "run_details/{runId}",
                arguments = listOf(
                    navArgument("runId") {
                        type = NavType.StringType
                    }
                )
            ) { backStackEntry ->

                val runId =
                    backStackEntry.arguments
                        ?.getString("runId")

                if (runId != null) {

                    val context = LocalContext.current

                    val application =
                        context.applicationContext
                                as RunningTrackerApplication

                    val viewModel: RunDetailsViewModel =
                        viewModel(
                            factory = RunDetailsViewModelFactory(
                                repository =
                                    application.runningRepository,
                                runId = runId
                            )
                        )

                    val uiState by
                    viewModel.uiState.collectAsStateWithLifecycle()

                    RunDetailsScreen(
                        uiState = uiState,
                        onBack = {
                            navController.popBackStack()
                        }
                    )
                }
            }
            composable(BottomNavItem.History.route) {
                val context = LocalContext.current
                val application =
                    context.applicationContext
                            as RunningTrackerApplication

                val viewModel: HistoryViewModel = viewModel(
                    factory = HistoryViewModelFactory(
                        application.runningRepository
                    )
                )
                HistoryScreen(
                    viewModel = viewModel,
                    onRunClick = { runId ->

                        navController.navigate(
                            "run_details/$runId"
                        )
                    }
                )
            }
        }
    }
}