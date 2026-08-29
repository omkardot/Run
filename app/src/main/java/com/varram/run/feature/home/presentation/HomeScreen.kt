package com.varram.run.feature.home.presentation

import androidx.compose.runtime.Composable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
@Composable
fun RunningTrackerDebugScreen(
    latitude: Double?,
    longitude: Double?,
    accuracy: Float?,
    isTracking: Boolean,
    onToggleTracking: () -> Unit
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    // Initialize MapView once
    val mapView = remember {
        MapView(context).apply {
            setTileSource(TileSourceFactory.MAPNIK)
            setMultiTouchControls(true)
            controller.setZoom(17.0)
        }
    }

    // Reuse a single Marker instance across recompositions
    val userMarker = remember {
        Marker(mapView).apply {
            title = "📍 YOU"
            setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
        }
    }

    // Bind MapView lifecycle properly to parent LifecycleOwner
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_RESUME -> mapView.onResume()
                Lifecycle.Event.ON_PAUSE -> mapView.onPause()
                else -> {}
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
            mapView.onDetach()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = "Running Tracker",
            style = MaterialTheme.typography.headlineMedium
        )

        // Map View Container using AndroidView update lambda
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(vertical = 16.dp)
        ) {
            AndroidView(
                factory = { mapView },
                modifier = Modifier.fillMaxSize(),
                update = { view ->
                    if (latitude != null && longitude != null) {
                        val point = GeoPoint(latitude, longitude)
                        userMarker.position = point

                        if (!view.overlays.contains(userMarker)) {
                            view.overlays.add(userMarker)
                        }

                        view.controller.animateTo(point)
                        view.invalidate()
                    }
                }
            )
        }

        // Location Telemetry Output
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Text(
                text = "Latitude: ${latitude ?: "N/A"}",
                fontFamily = FontFamily.Monospace
            )
            Text(
                text = "Longitude: ${longitude ?: "N/A"}",
                fontFamily = FontFamily.Monospace
            )
            Text(
                text = "Accuracy: ${accuracy?.let { "${it}m" } ?: "N/A"}",
                fontFamily = FontFamily.Monospace
            )
        }

        // Action Trigger
        Button(
            onClick = onToggleTracking,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = if (isTracking) "[ STOP RUN ]" else "[ START RUN ]",
                fontFamily = FontFamily.Monospace
            )
        }
    }
}