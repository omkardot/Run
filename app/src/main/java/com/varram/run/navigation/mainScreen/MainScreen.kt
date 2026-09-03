package com.varram.run.navigation.mainScreen

import android.Manifest
import android.content.Context
import android.os.Build
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.varram.run.RunningTrackerApplication
import com.varram.run.feature.details.presentation.RunDetailsScreen
import com.varram.run.feature.details.presentation.RunDetailsViewModel
import com.varram.run.feature.details.presentation.RunDetailsViewModelFactory
import com.varram.run.feature.history.presentation.HistoryScreen
import com.varram.run.feature.history.presentation.HistoryViewModel
import com.varram.run.feature.history.presentation.HistoryViewModelFactory
import com.varram.run.feature.history.presentation.formatDuration
import com.varram.run.feature.history.presentation.formatPace
import com.varram.run.feature.home.presentation.ActiveRunScreen
import com.varram.run.feature.home.presentation.HomeScreen
import com.varram.run.feature.home.presentation.RunningTrackerViewModel
import com.varram.run.feature.summary.presentation.RunSummaryScreen

@Composable
fun MainScreen(
    viewModel: RunningTrackerViewModel, context: Context
) {
    val navController = rememberNavController()

    val navBackStackEntry by
    navController.currentBackStackEntryAsState()
    val notificationPermissionLauncher =
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.RequestPermission()
        ) { isGranted ->

            if (isGranted) {
                Log.d("Permission", "Notification permission granted")
            } else {
                Log.d("Permission", "Notification permission denied")
            }
        }
    val currentRoute =
        navBackStackEntry?.destination?.route

    val showBottomBar =
        currentRoute in BottomNavItem.items.map { it.route }

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
//        bottomBar = {
//            if (showBottomBar) {
//                AppNavigationBar(
//                    navController = navController
//                )
//            }
//        }
    ) { innerPadding ->

        NavHost(
            navController = navController,
            startDestination = BottomNavItem.Home.route,
            modifier = Modifier.padding(innerPadding)
        ) {

            composable(BottomNavItem.Home.route) {
                HomeScreen(
                    weeklyDistanceKm = 34.2,
                    weeklyGoalKm = 50.0,
                    lastSessionDistanceKm = 5.2,
                    lastSessionTime = "28:45",
                    lastSessionPace = "5:31",
                    lastSessionDate = "Yesterday, 06:30 AM",
                    onStartRunClick = {
                        if (!uiState.isTracking) {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                                notificationPermissionLauncher.launch(
                                    Manifest.permission.POST_NOTIFICATIONS
                                )
                            }
                            viewModel.startTracking(context)
                        }
                        navController.navigate("active_run")
                    },
                    onHistoryClick = {
                        navController.navigate(BottomNavItem.History.route)
                    }
                )

//                RunningTrackerDebugScreen(
//                    latitude = uiState.latitude,
//                    longitude = uiState.longitude,
//                    accuracy = uiState.accuracy,
//                    isTracking = uiState.isTracking,
//                    routePoints = uiState.routePoints,
//                    distance = uiState.distanceMeters,
//                    paceperSec = uiState.paceSecondsPerKm,
//                    isPaused = uiState.isPaused,
//                    onTogglePause = {
//                        if (uiState.isPaused) {
//                            viewModel.resumeTracking(context)
//                        } else {
//                            viewModel.pauseTracking(context)
//                        }
//                    },
//
//                    onToggleTracking = {
//                        if (!uiState.isTracking) {
//                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
//                                notificationPermissionLauncher.launch(
//                                    Manifest.permission.POST_NOTIFICATIONS
//                                )
//                            }
//                            viewModel.startTracking(context)
//                        } else {
//                            viewModel.stopTracking(context)
//                        }
//                    }
//                )
            }
            composable("active_run") {
                ActiveRunScreen(
                    latitude = uiState.latitude,
                    longitude = uiState.longitude,
                    routePoints = uiState.routePoints,
                    distanceKm = (uiState.distanceMeters ?: 0.0) / 1000.0,
                    isPaused = uiState.isPaused,
                    paceSecondsPerKm = uiState.paceSecondsPerKm,
                    formattedTime = "",
                    onTogglePause = {
                        if (uiState.isPaused) {
                            viewModel.resumeTracking(context)
                        } else {
                            viewModel.pauseTracking(context)
                        }
                    },
                    onStopRun = {
                        viewModel.stopTracking(context)
                        navController.navigate("run_summary")
                    },
                    onBackClick = {
                        navController.popBackStack()
                    }
                )
            }
            composable("run_summary") {

                RunSummaryScreen(
                    distanceKm = uiState.distanceMeters / 1000.0,

                    durationFormatted =
                        formatDuration(uiState.elapsedTimeMillis),

                    avgPaceFormatted =
                        formatPace(uiState.paceSecondsPerKm ?:0.0),

                    routePoints = uiState.routePoints,

                    onSaveRun = {

                        navController.popBackStack(
                            BottomNavItem.Home.route,
                            inclusive = false
                        )
                    },

                    onDiscardRun = {

                        navController.popBackStack(
                            BottomNavItem.Home.route,
                            inclusive = false
                        )
                    },

                    onBackClick = {
                        navController.popBackStack()
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
                        onHomeClick = {
                            navController.navigate(BottomNavItem.Home.route)
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
                    onHomeClick= {

                    },
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