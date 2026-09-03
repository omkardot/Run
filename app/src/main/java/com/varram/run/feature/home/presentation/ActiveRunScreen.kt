package com.varram.run.feature.home.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.varram.run.data.model.RoutePoint
import com.varram.run.feature.history.presentation.formatPace
import kotlinx.coroutines.delay
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.Polyline

@Composable
fun ActiveRunScreen(
    latitude: Double?,
    longitude: Double?,
    routePoints: List<RoutePoint>,
    distanceKm: Double,
    formattedTime: String,
    paceSecondsPerKm: Double?,
    isPaused: Boolean,
    onStopRun: () -> Unit,
    onTogglePause: () -> Unit,
    onBackClick: () -> Unit
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    // --------------------------------------------------
    // MAP
    // --------------------------------------------------

    val mapView = remember {
        MapView(context).apply {
            setTileSource(TileSourceFactory.MAPNIK)
            setMultiTouchControls(true)
            controller.setZoom(16.5)
        }
    }

    // --------------------------------------------------
    // USER MARKER
    // --------------------------------------------------

    val userMarker = remember {
        Marker(mapView).apply {
            setAnchor(
                Marker.ANCHOR_CENTER,
                Marker.ANCHOR_CENTER
            )

            icon = ContextCompat.getDrawable(
                context,
                android.R.drawable.presence_online
            )
        }
    }

    // --------------------------------------------------
    // ROUTE POLYLINE
    // --------------------------------------------------

    val routePolyline = remember {
        Polyline(mapView).apply {
            outlinePaint.color =
                android.graphics.Color.parseColor("#CCFF00")

            outlinePaint.strokeWidth = 12f
        }
    }

    // --------------------------------------------------
    // MAP LIFECYCLE
    // --------------------------------------------------

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

            lifecycleOwner.lifecycle.removeObserver(
                observer
            )

            mapView.onDetach()
        }
    }

    // --------------------------------------------------
    // MAIN UI
    // --------------------------------------------------

    Box(
        modifier = Modifier.fillMaxSize()
    ) {

        // ==================================================
        // MAP BACKGROUND
        // ==================================================

        AndroidView(
            factory = {
                mapView
            },

            modifier = Modifier.fillMaxSize(),

            update = { view ->

                // -------------------------------
                // Route
                // -------------------------------

                val points =
                    routePoints.map {
                        GeoPoint(
                            it.latitude,
                            it.longitude
                        )
                    }

                routePolyline.setPoints(points)

                if (!view.overlays.contains(routePolyline)) {
                    view.overlays.add(routePolyline)
                }

                // -------------------------------
                // User location
                // -------------------------------

                if (
                    latitude != null &&
                    longitude != null
                ) {

                    val point =
                        GeoPoint(
                            latitude,
                            longitude
                        )

                    userMarker.position = point

                    if (!view.overlays.contains(userMarker)) {
                        view.overlays.add(userMarker)
                    }

                    view.controller.animateTo(point)
                }

                view.invalidate()
            }
        )

        // ==================================================
        // TOP/BOTTOM GRADIENT
        // ==================================================

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.Black.copy(alpha = 0.85f),
                            Color.Transparent,
                            Color.Transparent,
                            Color.Black.copy(alpha = 0.85f)
                        )
                    )
                )
        )

        // ==================================================
        // CONTENT
        // ==================================================

        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .padding(
                    horizontal = 16.dp,
                    vertical = 8.dp
                )
        ) {

            // ==================================================
            // HEADER
            // ==================================================

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment =
                    Alignment.CenterVertically
            ) {

                IconButton(
                    onClick = onBackClick
                ) {

                    Icon(
                        imageVector =
                            Icons.AutoMirrored.Filled.ArrowBack,

                        contentDescription =
                            "Back",

                        tint = Color.White
                    )
                }

                Text(
                    text = "Active Run",

                    fontSize = 20.sp,

                    fontWeight =
                        FontWeight.Bold,

                    color = Color.White,

                    modifier =
                        Modifier.padding(start = 8.dp)
                )
            }

            // ==================================================
            // PAUSED INDICATOR
            // ==================================================

            if (isPaused) {
                HoldToStopButton(
                    onStopConfirmed = {
                        onStopRun()
                    }
                )
            }

            // ==================================================
            // TELEMETRY CARD
            // ==================================================

            Card(
                colors =
                    CardDefaults.cardColors(
                        containerColor =
                            Color(0xCC1E1E1E)
                    ),

                shape =
                    RoundedCornerShape(16.dp),

                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        horizontal = 8.dp,
                        vertical = 8.dp
                    )
            ) {

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {

                    // -------------------------------
                    // Distance + Time
                    // -------------------------------

                    Row(
                        modifier =
                            Modifier.fillMaxWidth(),

                        horizontalArrangement =
                            Arrangement.SpaceBetween
                    ) {

                        // DISTANCE

                        Column {

                            Text(
                                text = "DISTANCE",

                                fontSize = 10.sp,

                                color = Color.Gray,

                                fontWeight =
                                    FontWeight.Bold,

                                fontFamily =
                                    FontFamily.Monospace
                            )

                            Row(
                                verticalAlignment =
                                    Alignment.Bottom
                            ) {

                                Text(
                                    text =
                                        String.format(
                                            "%.2f",
                                            distanceKm
                                        ),

                                    fontSize = 22.sp,

                                    fontWeight =
                                        FontWeight.Bold,

                                    color = Color.White,

                                    fontFamily =
                                        FontFamily.Monospace
                                )

                                Text(
                                    text = " km",

                                    fontSize = 12.sp,

                                    color = Color.White,

                                    modifier =
                                        Modifier.padding(
                                            bottom = 3.dp
                                        ),

                                    fontFamily =
                                        FontFamily.Monospace
                                )
                            }
                        }

                        // TIME

                        Column(
                            horizontalAlignment =
                                Alignment.End
                        ) {

                            Text(
                                text = "TIME",

                                fontSize = 10.sp,

                                color = Color.Gray,

                                fontWeight =
                                    FontWeight.Bold,

                                fontFamily =
                                    FontFamily.Monospace
                            )

                            Text(
                                text = formattedTime,

                                fontSize = 22.sp,

                                fontWeight =
                                    FontWeight.Bold,

                                color = Color.White,

                                fontFamily =
                                    FontFamily.Monospace
                            )
                        }
                    }

                    Spacer(
                        modifier =
                            Modifier.height(14.dp)
                    )

                    // -------------------------------
                    // Divider
                    // -------------------------------

                    HorizontalDivider(
                        color =
                            Color.White.copy(
                                alpha = 0.15f
                            )
                    )

                    Spacer(
                        modifier =
                            Modifier.height(12.dp)
                    )

                    // -------------------------------
                    // PACE
                    // -------------------------------

                    Text(
                        text = "PACE",

                        fontSize = 10.sp,

                        color = Color.Gray,

                        fontWeight =
                            FontWeight.Bold,

                        fontFamily =
                            FontFamily.Monospace
                    )

                    paceSecondsPerKm?.let {
                        Text(
                            text =
                                formatPace(
                                    it
                                ),

                            fontSize = 20.sp,

                            fontWeight =
                                FontWeight.Bold,

                            color = Color.White,

                            fontFamily =
                                FontFamily.Monospace
                        )
                    }
                }
            }

            // ==================================================
            // PUSH CONTROLS TO BOTTOM
            // ==================================================

            Spacer(
                modifier =
                    Modifier.weight(1f)
            )

            // ==================================================
            // PAUSE / RESUME BUTTON
            // ==================================================

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        bottom = 24.dp
                    ),

                contentAlignment =
                    Alignment.Center
            ) {

                IconButton(
                    onClick = onTogglePause,

                    modifier = Modifier
                        .size(72.dp)
                        .background(
                            Color(0xFFCCFF00),
                            CircleShape
                        )
                ) {

                    Icon(
                        imageVector =
                            if (isPaused) {
                                Icons.Default.PlayArrow
                            } else {
                                Icons.Default.Pause
                            },

                        contentDescription =
                            if (isPaused) {
                                "Resume"
                            } else {
                                "Pause"
                            },

                        tint = Color.Black,

                        modifier =
                            Modifier.size(36.dp)
                    )
                }
            }
        }
    }
}


@Composable
private fun HoldToStopButton(
    onStopConfirmed: () -> Unit
) {
    var isHolding by remember { mutableStateOf(false) }
    var progress by remember { mutableFloatStateOf(0f) }

    val holdDuration = 2000L

    LaunchedEffect(isHolding) {
        if (!isHolding) {
            progress = 0f
            return@LaunchedEffect
        }

        val startTime = System.currentTimeMillis()

        while (isHolding && progress < 1f) {
            val elapsed = System.currentTimeMillis() - startTime
            progress = (elapsed.toFloat() / holdDuration)
                .coerceIn(0f, 1f)

            delay(16)
        }

        if (progress >= 1f) {
            isHolding = false
            progress = 0f
            onStopConfirmed()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .clip(RoundedCornerShape(28.dp))
            .background(Color.Black.copy(alpha = 0.75f))
            .pointerInput(Unit) {
                detectTapGestures(
                    onPress = {
                        isHolding = true

                        try {
                            awaitRelease()
                        } finally {
                            isHolding = false
                        }
                    }
                )
            },
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .fillMaxWidth(progress)
                .background(Color.Red.copy(alpha = 0.35f))
                .align(Alignment.CenterStart)
        )

        Text(
            text = if (isHolding) {
                "KEEP HOLDING..."
            } else {
                "HOLD TO STOP"
            },
            color = Color.White,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp
        )
    }
}