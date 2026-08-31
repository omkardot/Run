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
import com.varram.run.data.model.RoutePoint
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.Polyline

@Composable
fun RunningTrackerDebugScreen(
    latitude: Double?,
    longitude: Double?,
    accuracy: Float?,
    isTracking: Boolean,
    routePoints: List<RoutePoint>,
    onToggleTracking: () -> Unit
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    // -----------------------------
    // MAP
    // -----------------------------

    val mapView = remember {
        MapView(context).apply {
            setTileSource(TileSourceFactory.MAPNIK)
            setMultiTouchControls(true)
            controller.setZoom(17.0)
        }
    }

    // -----------------------------
    // USER MARKER
    // -----------------------------

    val userMarker = remember {
        Marker(mapView).apply {
            title = "📍 YOU"
            setAnchor(
                Marker.ANCHOR_CENTER,
                Marker.ANCHOR_BOTTOM
            )
        }
    }

    // -----------------------------
    // RUNNING ROUTE POLYLINE
    // -----------------------------

    val routePolyline = remember {
        Polyline(mapView).apply {
            width = 8f
        }
    }

    // -----------------------------
    // MAP LIFECYCLE
    // -----------------------------

    DisposableEffect(lifecycleOwner) {

        val observer =
            LifecycleEventObserver { _, event ->

                when (event) {

                    Lifecycle.Event.ON_RESUME -> {
                        mapView.onResume()
                    }

                    Lifecycle.Event.ON_PAUSE -> {
                        mapView.onPause()
                    }

                    else -> Unit
                }
            }

        lifecycleOwner.lifecycle.addObserver(observer)

        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
            mapView.onDetach()
        }
    }

    // -----------------------------
    // UPDATE ROUTE
    // -----------------------------
    LaunchedEffect(routePoints) {

        val points = routePoints.map {
            GeoPoint(
                it.latitude,
                it.longitude
            )
        }

        routePolyline.setPoints(points)

        mapView.invalidate()
    }
    // -----------------------------
    // UI
    // -----------------------------

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

        // -----------------------------
        // MAP
        // -----------------------------

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

                    // Add polyline once
                    if (!view.overlays.contains(routePolyline)) {
                        view.overlays.add(routePolyline)
                    }

                    // Update marker
                    if (
                        latitude != null &&
                        longitude != null
                    ) {

                        val point = GeoPoint(
                            latitude,
                            longitude
                        )

                        userMarker.position = point

                        if (!view.overlays.contains(userMarker)) {
                            view.overlays.add(userMarker)
                        }

                        // Follow current location
                        view.controller.animateTo(point)
                    }

                    view.invalidate()
                }
            )
        }

        // -----------------------------
        // TELEMETRY
        // -----------------------------

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            horizontalAlignment = Alignment.Start
        ) {

            Text(
                text =
                    "Latitude: ${latitude ?: "N/A"}",
                fontFamily = FontFamily.Monospace
            )

            Text(
                text =
                    "Longitude: ${longitude ?: "N/A"}",
                fontFamily = FontFamily.Monospace
            )

            Text(
                text =
                    "Accuracy: ${
                        accuracy?.let { "${it}m" }
                            ?: "N/A"
                    }",
                fontFamily = FontFamily.Monospace
            )
        }

        // -----------------------------
        // START / STOP
        // -----------------------------

        Button(
            onClick = onToggleTracking,
            modifier = Modifier.fillMaxWidth()
        ) {

            Text(
                text =
                    if (isTracking) {
                        "[ STOP RUN ]"
                    } else {
                        "[ START RUN ]"
                    },
                fontFamily = FontFamily.Monospace
            )
        }
    }
}