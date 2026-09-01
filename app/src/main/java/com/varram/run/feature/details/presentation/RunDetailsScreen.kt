package com.varram.run.feature.details.presentation

import android.content.Context
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.varram.run.feature.details.components.RunMetric
import com.varram.run.feature.history.presentation.formatDate
import com.varram.run.feature.history.presentation.formatDuration
import com.varram.run.feature.history.presentation.formatPace
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.BoundingBox
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Polyline

@Composable
fun RunDetailsScreen(
    uiState: RunDetailsUiState,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    val mapView = remember {
        MapView(context).apply {
            setTileSource(TileSourceFactory.MAPNIK)
            setMultiTouchControls(true)
            controller.setZoom(16.0)
        }
    }

    val routePolyline = remember {
        Polyline().apply {
            title = "Run Route"
            width = 8f
        }
    }

    DisposableEffect(lifecycleOwner) {

        val observer =
            LifecycleEventObserver { _, event ->

                when (event) {
                    Lifecycle.Event.ON_RESUME ->
                        mapView.onResume()

                    Lifecycle.Event.ON_PAUSE ->
                        mapView.onPause()

                    else -> Unit
                }
            }

        lifecycleOwner.lifecycle.addObserver(observer)

        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
            mapView.onDetach()
        }
    }

    val geoPoints: List<GeoPoint> =
        uiState.routePoints.map { point ->
            GeoPoint(
                point.latitude,
                point.longitude
            )
        }

    Column(
        modifier = Modifier.fillMaxSize()
    ) {

        // -------------------------
        // TOP BAR
        // -------------------------

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    horizontal = 16.dp,
                    vertical = 12.dp
                ),
            verticalAlignment = Alignment.CenterVertically
        ) {

            TextButton(
                onClick = onBack
            ) {
                Text("Back")
            }

            Spacer(
                modifier = Modifier.weight(1f)
            )

            Text(
                text = "Run Details",
                style = MaterialTheme.typography.titleLarge
            )

            Spacer(
                modifier = Modifier.weight(1f)
            )

            Spacer(
                modifier = Modifier.width(60.dp)
            )
        }

        // -------------------------
        // MAP
        // -------------------------

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {

            AndroidView<MapView>(
                factory = {

                    if (!mapView.overlays.contains(routePolyline)) {
                        mapView.overlays.add(routePolyline)
                    }

                    mapView
                },

                modifier = Modifier.fillMaxSize(),

                update = { view ->

                    if (geoPoints.size >= 2) {

                        routePolyline.setPoints(geoPoints)

                        val boundingBox =
                            BoundingBox.fromGeoPoints(geoPoints)

                        view.zoomToBoundingBox(
                            boundingBox,
                            true,
                            80
                        )
                    }

                    view.invalidate()
                }
            )
        }

        // -------------------------
        // RUN INFORMATION
        // -------------------------

        uiState.run?.let { run ->

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {

                Text(
                    text = formatDate(
                        run.startTime
                    ),
                    style = MaterialTheme.typography.titleMedium
                )

                Spacer(
                    modifier = Modifier.height(16.dp)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement =
                        Arrangement.SpaceBetween
                ) {

                    RunMetric(
                        label = "DISTANCE",
                        value = String.format(
                            "%.2f km",
                            run.distanceMeters / 1000.0
                        )
                    )

                    RunMetric(
                        label = "TIME",
                        value = formatDuration(
                            run.durationMillis
                        )
                    )

                    RunMetric(
                        label = "PACE",
                        value = run.avgPaceSecondsPerKm
                            ?.let {
                                "${formatPace(it)} /km"
                            }
                            ?: "--"
                    )
                }
            }
        }
    }
}