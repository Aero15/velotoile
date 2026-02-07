package xyz.doocode.velotoile.ui.components

import SortField
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.*
import androidx.compose.ui.unit.dp
import xyz.doocode.velotoile.core.dto.Station
import xyz.doocode.velotoile.ui.components.search.result.StationItem

@Composable
fun StationsList(
    modifier: Modifier = Modifier,
    stations: List<Station>,
    onStationClick: (Station) -> Unit = {},
    sortField: SortField = SortField.NUMBER,
    isSearching: Boolean = false,
    contentPadding: PaddingValues = PaddingValues(0.dp),
    state: LazyListState = rememberLazyListState()
) {
    if (stations.isEmpty()) {
        Box(
            modifier = modifier
                .fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = if (isSearching) "Aucun résultat" else "Aucune station disponible",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    } else {
        LazyColumn(
            modifier = modifier
                .fillMaxSize(),
            contentPadding = contentPadding,
            state = state
        ) {
            item {
                Text(
                    text = if (isSearching) {
                        "${stations.size} résultat${if (stations.size > 1) "s" else ""} trouvé${if (stations.size > 1) "s" else ""}"
                    } else {
                        "${stations.size} station${if (stations.size > 1) "s" else ""}"
                    },
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .padding(vertical = 12.dp)
                )
            }
            
            items(stations) { station ->
                StationItem(
                    station = station,
                    onStationClick = onStationClick,
                    modifier = Modifier.fillMaxWidth(),
                    showMechanicalBikes = sortField == SortField.MECHANICAL_BIKES,
                    showElectricalBikes = sortField == SortField.ELECTRICAL_BIKES
                )
            }
        }
    }
}
