package xyz.doocode.velotoile.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.SortByAlpha
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.saveable.rememberSaveable
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

    // Local search state for bookmarks (do not modify the global search query)
    var bookmarkSearchQuery by rememberSaveable { mutableStateOf("") }







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
                searchQuery = bookmarkSearchQuery,
                onSearchQueryChanged = { bookmarkSearchQuery = it },
                onCloseSearch = { isSearching = false; bookmarkSearchQuery = "" },
                modifier = Modifier
                    .background(Color(0xFF00999d))
            )
        }

        // Apply local search filter to favorites
        val displayedFavorites = if (bookmarkSearchQuery.trim().isNotEmpty()) {
            val q = bookmarkSearchQuery.trim().lowercase()
            favoriteStations.value.filter { station ->
                station.name.lowercase().contains(q) || station.address.lowercase().contains(q)
            }
        } else {
            favoriteStations.value
        }

        when (displayedFavorites.isEmpty()) {
            true -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(
                        text = if (isSearching) "Aucun résultat" else "Aucun favori",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            false -> {
                StationsList(
                    stations = displayedFavorites,
                    modifier = Modifier.fillMaxSize(),
                    onStationClick = { station -> selectedStation = station },
                    sortField = currentSortField.value,
                    isSearching = isSearching
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