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
import androidx.compose.ui.unit.dp
import androidx.core.util.TimeUtils.formatDuration
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.varram.run.data.local.entity.RunEntity
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun HistoryScreen(
    viewModel: HistoryViewModel,
    onRunClick: (String) -> Unit
){
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

        items(
            items = uiState.runs,
            key = { it.runId }
        ) { run ->

            RunHistoryItem(
                run = run,
                onClick = {
                    onRunClick(run.runId)
                }
            )
        }
    }
}

@Composable
fun RunHistoryItem(
    run: RunEntity,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp)
            .clickable(onClick = onClick)
    ) {
        Text(
            text = formatDate(run.startTime)
        )

        Text(
            text = "${"%.2f".format(run.distanceMeters / 1000.0)} km"
        )

        Text(
            text = formatDuration(run.durationMillis)
        )

        run.avgPaceSecondsPerKm?.let { pace ->
            Text(
                text = "${formatPace(pace)} /km"
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