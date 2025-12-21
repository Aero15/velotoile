package xyz.doocode.velotoile.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.*
import xyz.doocode.velotoile.core.dto.Station
import xyz.doocode.velotoile.ui.components.search.result.StationItem

@Composable
fun StationsList(
    modifier: Modifier = Modifier,
    stations: List<Station>,
    onStationClick: (Station) -> Unit = {},
) {
    if (stations.isEmpty()) {
        Box(
            modifier = modifier
                .fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Aucune station disponible",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    } else {
        LazyColumn(
            modifier = modifier
                .fillMaxSize(),
        ) {
            items(stations) { station ->
                StationItem(
                    station = station,
                    onStationClick = onStationClick,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}
