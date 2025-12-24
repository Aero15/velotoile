package xyz.doocode.velotoile.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.SortByAlpha
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import xyz.doocode.velotoile.core.dto.Station
import xyz.doocode.velotoile.ui.components.StationsList
import xyz.doocode.velotoile.ui.components.details.StationDetailsSheet
import xyz.doocode.velotoile.ui.components.search.SearchBar
import xyz.doocode.velotoile.ui.components.search.menu.SortMenu
import xyz.doocode.velotoile.ui.viewmodel.StationsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookmarksScreen(viewModel: StationsViewModel, modifier: Modifier = Modifier) {


    var isSearching by remember { mutableStateOf(false) }
    var showSortMenu by remember { mutableStateOf(false) }
    var selectedStation by remember { mutableStateOf<Station?>(null) }

    val filteredStations = viewModel.filteredStations.observeAsState(emptyList())
    val searchQuery = viewModel.searchQuery.observeAsState("")







    val favoriteStations = viewModel.favoriteStations.observeAsState(emptyList())
    val favoriteNumbers = viewModel.favoriteNumbers.observeAsState(emptySet())
    val currentSortField = viewModel.sortField.observeAsState(SortField.NUMBER)

    Column(modifier = modifier.fillMaxSize()) {
        if (!isSearching) {
            TopAppBar(
                modifier = Modifier.padding(0.dp),
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF00999d), // light:00abc4, dark:00999d
                ),
                windowInsets = WindowInsets(top = 0.dp),
                title = { Text("Favoris") },
                actions = {
                    IconButton(
                        onClick = { showSortMenu = !showSortMenu }
                    ) {
                        Icon(Icons.Filled.SortByAlpha, contentDescription = "Tri")
                    }

                    SortMenu(
                        isMenuOpen = showSortMenu,
                        onMenuOpenChange = { showSortMenu = it },
                        viewModel = viewModel,
                        modifier = Modifier.padding(top = 32.dp)
                    )

                    IconButton(
                        onClick = { isSearching = !isSearching }
                    ) {
                        Icon(Icons.Filled.Search, contentDescription = "Recherche")
                    }
                }
            )
        } else {
            SearchBar(
                transparent = true,
                searchQuery = searchQuery.value,
                onSearchQueryChanged = { viewModel.setSearchQuery(it) },
                onCloseSearch = { isSearching = false },
                modifier = Modifier
                    .background(Color(0xFF00999d))
            )
        }

        // Small info line to verify favorites are loaded correctly
        Text(
            text = "${favoriteStations.value.size} stations favorites",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        )

        when (favoriteStations.value.isEmpty()) {
            true -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Aucun favori", style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
            false -> {
                StationsList(
                    stations = favoriteStations.value,
                    modifier = Modifier.fillMaxSize(),
                    onStationClick = { station -> selectedStation = station },
                    sortField = currentSortField.value,
                    isSearching = false
                )
            }
        }
    }

    // Station details sheet (allow un-favoriting from details)
    StationDetailsSheet(
        station = selectedStation,
        onDismiss = { selectedStation = null },
        onToggleFavorite = { stationNumber -> viewModel.toggleFavorite(stationNumber) }
    )
}