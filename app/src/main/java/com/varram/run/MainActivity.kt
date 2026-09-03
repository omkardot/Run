package com.varram.run

import android.Manifest
import android.R.attr.action
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import com.varram.run.core.permissions.RequestLocationPermission
import com.varram.run.feature.home.presentation.RunningTrackerViewModel
import com.varram.run.navigation.mainScreen.MainScreen
import com.varram.run.service.LocationTrackingService
import com.varram.run.ui.theme.RunTheme
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        val repository =
            (application as RunningTrackerApplication)
                .runningRepository
        val viewModel = RunningTrackerViewModel(repository)

        setContent {
            RunTheme {
                val context = LocalContext.current
                MainScreen(
                    viewModel = viewModel,
                    context
                )


                // Launcher to handle the system permission dialog response
                /*val permissionLauncher = rememberLauncherForActivityResult(
                    contract = ActivityResultContracts.RequestMultiplePermissions()
                ) { permissions ->
                    val fineGranted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] ?: false
                    val coarseGranted = permissions[Manifest.permission.ACCESS_COARSE_LOCATION] ?: false

                    if (fineGranted || coarseGranted) {
                        startLocationTrackingService()
                    }
                }

                // Check permission on initial launch
                LaunchedEffect(Unit) {
                    val hasFine = ContextCompat.checkSelfPermission(
                        context,
                        Manifest.permission.ACCESS_FINE_LOCATION
                    ) == PackageManager.PERMISSION_GRANTED

                    val hasCoarse = ContextCompat.checkSelfPermission(
                        context,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    ) == PackageManager.PERMISSION_GRANTED

                    if (hasFine || hasCoarse) {
                        startLocationTrackingService()
                    } else {
                        permissionLauncher.launch(
                            arrayOf(
                                Manifest.permission.ACCESS_FINE_LOCATION,
                                Manifest.permission.ACCESS_COARSE_LOCATION
                            )
                        )
                    }
                }*/


            }
        }
    }

}

