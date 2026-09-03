package com.varram.run.feature.history.presentation

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.core.util.TimeUtils.formatDuration
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.varram.run.data.local.entity.RunEntity
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
// Android & Core Utilities
import android.graphics.Color as AndroidColor
import androidx.core.content.ContextCompat

// Compose Foundation & Layout
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape

// Compose Material 3
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.material3.Text

// Compose Icons
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DirectionsRun
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Sensors
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember

import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView

// Lifecycle
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.varram.run.data.local.entity.RunStatus
import com.varram.run.data.model.RoutePoint

// Osmdroid Map Imports
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.Polyline
import java.util.Calendar
@Composable
fun HistoryScreen(
    viewModel: HistoryViewModel,
    onRunClick: (String) -> Unit,
    onHomeClick: () -> Unit,
    onFilterClick: () -> Unit = {},
    onProfileClick: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

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

            HistoryTopBar(
                onProfileClick = onProfileClick
            )

            // ============================================================
            // CONTENT
            // ============================================================

            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentPadding = PaddingValues(
                    top = 14.dp,
                    bottom = 16.dp
                )
            ) {

                // --------------------------------------------------------
                // HEADER
                // --------------------------------------------------------

                item {

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {

                        Text(
                            text = "Recent Runs",
                            color = Color.White,
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(
                            modifier = Modifier.weight(1f)
                        )

                        IconButton(
                            onClick = onFilterClick,
                            modifier = Modifier
                                .size(40.dp)
                                .background(
                                    Color(0xFF262626),
                                    CircleShape
                                )
                        ) {
                            Icon(
                                imageVector = Icons.Default.FilterList,
                                contentDescription = "Filter",
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }

                    Spacer(
                        modifier = Modifier.height(16.dp)
                    )
                }

                // --------------------------------------------------------
                // LOADING
                // --------------------------------------------------------

                if (uiState.runs.isEmpty()) {

                    // ----------------------------------------------------
                    // EMPTY STATE
                    // ----------------------------------------------------

                    item {
                        EmptyHistoryState()
                    }

                } else {

                    // ----------------------------------------------------
                    // RUN LIST
                    // ----------------------------------------------------

                    items(
                        items = uiState.runs,
                        key = { it.runId }
                    ) { run ->

                        HistoryCardItem(
                            run = run,
                            onClick = {
                                onRunClick(run.runId)
                            }
                        )

                        Spacer(
                            modifier = Modifier.height(12.dp)
                        )
                    }
                }
            }

            // ============================================================
            // BOTTOM NAV
            // ============================================================

            HistoryBottomNavigation(
                onHomeClick = onHomeClick
            )
        }
    }
}
fun formatDuration(
    durationMillis: Long
): String {

    val totalSeconds =
        durationMillis / 1000

    val hours =
        totalSeconds / 3600

    val minutes =
        (totalSeconds % 3600) / 60

    val seconds =
        totalSeconds % 60

    return if (hours > 0) {
        String.format(
            Locale.getDefault(),
            "%d:%02d:%02d",
            hours,
            minutes,
            seconds
        )
    } else {
        String.format(
            Locale.getDefault(),
            "%02d:%02d",
            minutes,
            seconds
        )
    }
}
fun formatDate(
    timestamp: Long
): String {

    val formatter =
        SimpleDateFormat(
            "dd MMM yyyy, hh:mm a",
            Locale.getDefault()
        )

    return formatter.format(
        Date(timestamp)
    )
}
fun formatPace(
    paceSecondsPerKm: Double
): String {

    val totalSeconds =
        paceSecondsPerKm.toInt()

    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60

    return String.format(
        Locale.getDefault(),
        "%d:%02d",
        minutes,
        seconds
    )
}
@Composable
private fun HistoryTopBar(
    onProfileClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.Black)
            .statusBarsPadding()
            .padding(
                horizontal = 16.dp,
                vertical = 10.dp
            ),
        verticalAlignment = Alignment.CenterVertically
    ) {

        Surface(
            modifier = Modifier.size(28.dp),
            color = Color(0xFF1E2923),
            shape = RoundedCornerShape(6.dp)
        ) {
            Icon(
                imageVector = Icons.Default.DirectionsRun,
                contentDescription = null,
                tint = Color(0xFFCCFF00),
                modifier = Modifier.padding(5.dp)
            )
        }

        Spacer(
            modifier = Modifier.width(10.dp)
        )

        Text(
            text = "History",
            color = Color.White,
            fontSize = 21.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(
            modifier = Modifier.weight(1f)
        )

        IconButton(
            onClick = onProfileClick,
            modifier = Modifier
                .size(32.dp)
                .background(
                    Color.White,
                    CircleShape
                )
        ) {

            Icon(
                imageVector = Icons.Default.Person,
                contentDescription = "Profile",
                tint = Color.Black,
                modifier = Modifier.size(19.dp)
            )
        }
    }
}
@Composable
private fun HistoryCardItem(
    run: RunEntity,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF1E1E1E)
        )
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            // ============================================================
            // MAP PLACEHOLDER
            // ============================================================

            Box(
                modifier = Modifier
                    .size(82.dp)
                    .clip(
                        RoundedCornerShape(12.dp)
                    )
                    .background(
                        Color(0xFF292929)
                    ),
                contentAlignment = Alignment.Center
            ) {

                Icon(
                    imageVector = Icons.Default.Map,
                    contentDescription = "Run route",
                    tint = Color(0xFF555555),
                    modifier = Modifier.size(30.dp)
                )

                // Small RUN indicator

                Box(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(6.dp)
                        .size(8.dp)
                        .background(
                            Color(0xFFCCFF00),
                            CircleShape
                        )
                )
            }

            Spacer(
                modifier = Modifier.width(14.dp)
            )

            // ============================================================
            // RUN INFORMATION
            // ============================================================

            Column(
                modifier = Modifier.weight(1f)
            ) {

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.Top
                ) {

                    Text(
                        text = "Run",
                        color = Color.White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold
                    )

                    Spacer(
                        modifier = Modifier.weight(1f)
                    )

                    Text(
                        text = formatHistoryDate(
                            run.startTime
                        ),
                        color = Color.Gray,
                        fontSize = 11.sp
                    )
                }

                Spacer(
                    modifier = Modifier.height(10.dp)
                )

                Row(
                    verticalAlignment = Alignment.Bottom
                ) {

                    // Distance

                    Column {

                        Text(
                            text = String.format(
                                Locale.US,
                                "%.2f",
                                run.distanceMeters / 1000.0
                            ),
                            color = Color.White,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )

                        Text(
                            text = "KM",
                            color = Color(0xFFCCFF00),
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(
                        modifier = Modifier.width(22.dp)
                    )

                    // Duration

                    Column {

                        Text(
                            text = formatDuration(
                                run.durationMillis
                            ),
                            color = Color.White,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )

                        Text(
                            text = "TIME",
                            color = Color.Gray,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(
                        modifier = Modifier.weight(1f)
                    )

                    // Arrow

                    Text(
                        text = "›",
                        color = Color(0xFF666666),
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Light
                    )
                }
            }
        }
    }
}
@Composable
private fun EmptyHistoryState() {

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(300.dp)
            .padding(horizontal = 32.dp),
        contentAlignment = Alignment.Center
    ) {

        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Icon(
                imageVector = Icons.Default.History,
                contentDescription = null,
                tint = Color(0xFF555555),
                modifier = Modifier.size(52.dp)
            )

            Spacer(
                modifier = Modifier.height(16.dp)
            )

            Text(
                text = "No runs yet",
                color = Color.White,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(
                modifier = Modifier.height(6.dp)
            )

            Text(
                text = "Complete your first run\nand it will appear here.",
                color = Color.Gray,
                fontSize = 13.sp,
                textAlign = TextAlign.Center,
                lineHeight = 19.sp
            )
        }
    }
}
@Composable
private fun HistoryBottomNavigation(
    onHomeClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(60.dp)
            .background(Color(0xFF1E1E1E))
    ) {

        // HOME

        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
                .clickable {
                    onHomeClick()
                },
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            Icon(
                imageVector = Icons.Default.Home,
                contentDescription = "Home",
                tint = Color.Gray,
                modifier = Modifier.size(20.dp)
            )

            Spacer(
                modifier = Modifier.height(2.dp)
            )

            Text(
                text = "HOME",
                color = Color.Gray,
                fontSize = 8.sp,
                fontWeight = FontWeight.Bold
            )
        }

        // HISTORY ACTIVE

        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
                .background(Color(0xFF252A13)),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            Icon(
                imageVector = Icons.Default.History,
                contentDescription = "History",
                tint = Color(0xFFCCFF00),
                modifier = Modifier.size(20.dp)
            )

            Spacer(
                modifier = Modifier.height(2.dp)
            )

            Text(
                text = "HISTORY",
                color = Color(0xFFCCFF00),
                fontSize = 8.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun RunRouteThumbnail(
    routePoints: List<RoutePoint>,
    isTreadmill: Boolean
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    if (isTreadmill) {

        Box(
            modifier = Modifier
                .size(76.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(Color(0xFF2B2B2B)),
            contentAlignment = Alignment.Center
        ) {

            Icon(
                imageVector = Icons.Default.Sensors,
                contentDescription = "Treadmill",
                tint = Color.Gray,
                modifier = Modifier.size(30.dp)
            )
        }

        return
    }

    val mapView = remember {

        MapView(context).apply {

            setTileSource(
                TileSourceFactory.MAPNIK
            )

            setMultiTouchControls(false)

            controller.setZoom(14.0)

            setBuiltInZoomControls(false)
        }
    }

    val polyline = remember {

        Polyline(mapView).apply {

            outlinePaint.color =
                android.graphics.Color.parseColor(
                    "#CCFF00"
                )

            outlinePaint.strokeWidth = 7f
        }
    }

    LaunchedEffect(routePoints) {

        if (routePoints.isNotEmpty()) {

            val geoPoints =
                routePoints.map {
                    GeoPoint(
                        it.latitude,
                        it.longitude
                    )
                }

            polyline.setPoints(
                geoPoints
            )

            if (geoPoints.size == 1) {

                mapView.controller.setCenter(
                    geoPoints.first()
                )

            } else {

                val boundingBox =
                    org.osmdroid.util.BoundingBox
                        .fromGeoPoints(
                            geoPoints
                        )

                mapView.zoomToBoundingBox(
                    boundingBox,
                    true,
                    10
                )
            }
        }

        mapView.invalidate()
    }

    DisposableEffect(
        lifecycleOwner
    ) {

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

        lifecycleOwner.lifecycle.addObserver(
            observer
        )

        onDispose {

            lifecycleOwner.lifecycle.removeObserver(
                observer
            )

            mapView.onPause()
        }
    }

    Box(
        modifier = Modifier
            .size(76.dp)
            .clip(
                RoundedCornerShape(12.dp)
            )
            .background(
                Color(0xFF2B2B2B)
            )
    ) {

        AndroidView(
            factory = {
                mapView
            },
            modifier = Modifier.fillMaxSize(),
            update = { view ->

                if (
                    !view.overlays.contains(
                        polyline
                    )
                ) {
                    view.overlays.add(
                        polyline
                    )
                }

                view.invalidate()
            }
        )

        // Dark overlay for consistency
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Color.Black.copy(
                        alpha = 0.08f
                    )
                )
        )
    }
}


fun formatHistoryDate(
    timestamp: Long
): String {

    val runDate = Calendar.getInstance().apply {
        timeInMillis = timestamp
    }

    val today = Calendar.getInstance()

    if (
        runDate.get(Calendar.YEAR) ==
        today.get(Calendar.YEAR) &&
        runDate.get(Calendar.DAY_OF_YEAR) ==
        today.get(Calendar.DAY_OF_YEAR)
    ) {
        return "Today"
    }

    val yesterday = Calendar.getInstance().apply {
        add(Calendar.DAY_OF_YEAR, -1)
    }

    if (
        runDate.get(Calendar.YEAR) ==
        yesterday.get(Calendar.YEAR) &&
        runDate.get(Calendar.DAY_OF_YEAR) ==
        yesterday.get(Calendar.DAY_OF_YEAR)
    ) {
        return "Yesterday"
    }

    return SimpleDateFormat(
        "MMM dd",
        Locale.getDefault()
    ).format(
        Date(timestamp)
    )
}