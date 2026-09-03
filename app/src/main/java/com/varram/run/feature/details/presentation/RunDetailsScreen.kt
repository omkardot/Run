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
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.LifecycleOwner
import com.varram.run.data.model.RoutePoint
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
/*

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
}*/
@Composable
fun RunDetailsScreen(
    uiState: RunDetailsUiState,
    onHomeClick: () -> Unit = {}
) {
    val context = LocalContext.current

    val mapView = remember {
        MapView(context).apply {
            setTileSource(TileSourceFactory.MAPNIK)
            setMultiTouchControls(true)
            controller.setZoom(13.5)
        }
    }

    val routePolyline = remember {
        Polyline(mapView).apply {
            outlinePaint.color =
                android.graphics.Color.parseColor("#CCFF00")
            outlinePaint.strokeWidth = 8f
        }
    }

    /*
     * Update route whenever routePoints changes
     */
    LaunchedEffect(uiState.routePoints) {

        if (uiState.routePoints.isNotEmpty()) {

            val points = uiState.routePoints.map {
                GeoPoint(
                    it.latitude,
                    it.longitude
                )
            }

            routePolyline.setPoints(points)

            /*
             * Center map around the route
             */
            val middlePoint =
                points[points.size / 2]

            mapView.controller.setCenter(middlePoint)

            mapView.controller.setZoom(13.5)
        }

        mapView.invalidate()
    }

    /*
     * Map lifecycle
     */
    DisposableEffect(Unit) {

        val lifecycleOwner =
            context as? LifecycleOwner

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

        lifecycleOwner?.lifecycle?.addObserver(observer)

        onDispose {
            lifecycleOwner?.lifecycle?.removeObserver(observer)
            mapView.onPause()
        }
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color(0xFF121212)
    ) {

        Column(
            modifier = Modifier.fillMaxSize()
        ) {

            // ============================================================
            // TOP BAR
            // ============================================================

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.Black)
                    .statusBarsPadding()
                    .padding(
                        horizontal = 14.dp,
                        vertical = 10.dp
                    ),
                verticalAlignment = Alignment.CenterVertically
            ) {

                /*
                 * App icon
                 */
                Box(
                    modifier = Modifier
                        .size(26.dp)
                        .clip(RoundedCornerShape(5.dp))
                        .background(Color(0xFF202020)),
                    contentAlignment = Alignment.Center
                ) {

                    Text(
                        text = "▣",
                        color = Color(0xFFCCFF00),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(
                    modifier = Modifier.width(9.dp)
                )

                Text(
                    text = "History",
                    color = Color.White,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(
                    modifier = Modifier.weight(1f)
                )

                /*
                 * Profile button
                 */
                IconButton(
                    onClick = {  },
                    modifier = Modifier
                        .size(30.dp)
                        .background(
                            Color.White,
                            CircleShape
                        )
                ) {

                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = "Profile",
                        tint = Color.Black,
                        modifier = Modifier.size(17.dp)
                    )
                }
            }

            // ============================================================
            // MAP
            // ============================================================

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {

                AndroidView(
                    factory = {
                        mapView.apply {

                            // Add polyline once
                            if (!overlays.contains(routePolyline)) {
                                overlays.add(routePolyline)
                            }
                        }
                    },
                    modifier = Modifier.fillMaxSize(),
                    update = { view ->

                        val points = uiState.routePoints.map {
                            GeoPoint(
                                it.latitude,
                                it.longitude
                            )
                        }

                        if (points.isNotEmpty()) {

                            // Update route
                            routePolyline.setPoints(points)

                            // Center map on route
                            if (points.size == 1) {
                                view.controller.setCenter(points.first())
                                view.controller.setZoom(16.0)
                            } else {

                                val boundingBox =
                                    BoundingBox.fromGeoPoints(points)

                                view.zoomToBoundingBox(
                                    boundingBox,
                                    true,
                                    80
                                )
                            }
                        }

                        view.invalidate()
                    }
                )

                // Gradient
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                        .align(Alignment.BottomCenter)
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    Color.Transparent,
                                    Color(0xFF121212)
                                )
                            )
                        )
                )


                // ========================================================
                // RUN STAT CARD
                // ========================================================

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(
                            horizontal = 12.dp
                        )
                        .align(Alignment.BottomCenter),
                    shape = RoundedCornerShape(
                        topStart = 12.dp,
                        topEnd = 12.dp,
                        bottomStart = 0.dp,
                        bottomEnd = 0.dp
                    ),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFF1E1E1E)
                    )
                ) {

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(
                                horizontal = 12.dp,
                                vertical = 10.dp
                            )
                    ) {

                        // ------------------------------------------------
                        // DISTANCE
                        // ------------------------------------------------

                        Row(
                            verticalAlignment =
                                Alignment.Bottom
                        ) {

                            Text(
                                text = String.format(
                                    Locale.US,
                                    "%.2f",
                                    uiState.run?.distanceMeters?.div(1000.0)
                                ),
                                color = Color(0xFFCCFF00),
                                fontSize = 38.sp,
                                fontWeight =
                                    FontWeight.ExtraBold
                            )

                            Text(
                                text = " KM",
                                color = Color.White,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(
                                    bottom = 7.dp,
                                    start = 4.dp
                                )
                            )
                        }

                        Spacer(
                            modifier = Modifier.height(10.dp)
                        )

                        // ------------------------------------------------
                        // PACE + TIME
                        // ------------------------------------------------

                        Row(
                            modifier = Modifier.fillMaxWidth()
                        ) {

                            Column(
                                modifier = Modifier.weight(1f)
                            ) {

                                Text(
                                    text = "PACE",
                                    color = Color.White,
                                    fontSize = 8.sp,
                                    fontWeight =
                                        FontWeight.Bold,
                                    letterSpacing = 1.sp
                                )

                                Spacer(
                                    modifier =
                                        Modifier.height(3.dp)
                                )

                                uiState.run?.avgPaceSecondsPerKm?.let {
                                    Text(
                                        text = formatPace(
                                            it
                                        ) + " /km",
                                        color = Color.White,
                                        fontSize = 15.sp,
                                        fontWeight =
                                            FontWeight.Bold
                                    )
                                }
                            }

                            Column(
                                horizontalAlignment =
                                    Alignment.End
                            ) {

                                Text(
                                    text = "TIME",
                                    color = Color.White,
                                    fontSize = 8.sp,
                                    fontWeight =
                                        FontWeight.Bold,
                                    letterSpacing = 1.sp
                                )

                                Spacer(
                                    modifier =
                                        Modifier.height(3.dp)
                                )

                                uiState.run?.durationMillis?.let {
                                    Text(
                                        text = formatDuration(
                                            it
                                        ),
                                        color = Color.White,
                                        fontSize = 15.sp,
                                        fontWeight =
                                            FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // ============================================================
            // RUN META DATA
            // ============================================================

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF121212))
                    .padding(
                        top = 8.dp,
                        bottom = 10.dp,
                        start = 12.dp,
                        end = 12.dp
                    )
            ) {

                Column(
                    modifier = Modifier.weight(1f)
                ) {

                    Text(
                        text = "START TIME",
                        color = Color.White,
                        fontSize = 7.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.8.sp
                    )

                    Spacer(
                        modifier = Modifier.height(2.dp)
                    )

                    uiState.run?.startTime?.let {
                        Text(
                            text = formatStartTime(it),
                            color = Color(0xFFCCFF00),
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Column(
                    horizontalAlignment =
                        Alignment.End
                ) {

                    Text(
                        text = "DATE",
                        color = Color.White,
                        fontSize = 7.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.8.sp
                    )

                    Spacer(
                        modifier = Modifier.height(2.dp)
                    )

                    uiState.run?.startTime?.let {
                        Text(
                            text = formatRunDate(it),
                            color = Color.White,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            // ============================================================
            // BOTTOM NAVIGATION
            // ============================================================

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .background(Color(0xFF1A1A1A))
            ) {

                // --------------------------------------------------------
                // HOME
                // --------------------------------------------------------

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .clickable {
                            onHomeClick()
                        },
                    contentAlignment = Alignment.Center
                ) {

                    Column(
                        horizontalAlignment =
                            Alignment.CenterHorizontally
                    ) {

                        Icon(
                            imageVector =
                                Icons.Default.Home,
                            contentDescription = "Home",
                            tint = Color(0xFF777777),
                            modifier = Modifier.size(18.dp)
                        )

                        Spacer(
                            modifier = Modifier.height(2.dp)
                        )

                        Text(
                            text = "HOME",
                            color = Color(0xFF777777),
                            fontSize = 7.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                // --------------------------------------------------------
                // HISTORY - ACTIVE
                // --------------------------------------------------------

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .background(Color(0xFF252A13)),
                    contentAlignment = Alignment.Center
                ) {

                    Column(
                        horizontalAlignment =
                            Alignment.CenterHorizontally
                    ) {

                        Icon(
                            imageVector =
                                Icons.Default.History,
                            contentDescription = "History",
                            tint = Color(0xFFCCFF00),
                            modifier = Modifier.size(18.dp)
                        )

                        Spacer(
                            modifier = Modifier.height(2.dp)
                        )

                        Text(
                            text = "HISTORY",
                            color = Color(0xFFCCFF00),
                            fontSize = 7.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}
fun formatStartTime(timestamp: Long): String {

    val formatter =
        SimpleDateFormat(
            "h:mm a",
            Locale.getDefault()
        )

    return formatter.format(
        Date(timestamp)
    )
}
fun formatRunDate(timestamp: Long): String {

    val formatter =
        SimpleDateFormat(
            "MMM dd, yyyy",
            Locale.getDefault()
        )

    return formatter.format(
        Date(timestamp)
    )
}