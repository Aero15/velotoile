package xyz.doocode.velotoile.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.SortByAlpha
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import xyz.doocode.velotoile.core.dto.Station
import xyz.doocode.velotoile.ui.components.dashboard.FavoriteStationTile
import xyz.doocode.velotoile.ui.components.details.StationDetailsSheet
import xyz.doocode.velotoile.ui.components.search.SearchBar
import xyz.doocode.velotoile.ui.components.search.menu.SortMenu
import xyz.doocode.velotoile.ui.viewmodel.StationsViewModel
import xyz.doocode.velotoile.ui.components.common.LocationFab
import xyz.doocode.velotoile.ui.components.common.RateLimitedPullToRefresh
import xyz.doocode.velotoile.ui.components.common.RefreshSuccessObserver

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookmarksScreen(viewModel: StationsViewModel, modifier: Modifier = Modifier) {
    var isSearching by remember { mutableStateOf(false) }
    var showSortMenu by remember { mutableStateOf(false) }
    var selectedStation by remember { mutableStateOf<Station?>(null) }
    var bookmarkSearchQuery by rememberSaveable { mutableStateOf("") }

    val favoriteStations = viewModel.favoriteStations.observeAsState(emptyList())
    val largeTileStations = viewModel.largeTileStations.observeAsState(emptySet())
    val stationsResource = viewModel.stations.observeAsState()
    val sortField = viewModel.sortField.observeAsState()
    val sortOrder = viewModel.sortOrder.observeAsState()
    val userLocation = viewModel.userLocation.observeAsState()
    
    val gridState = rememberLazyGridState()

    LaunchedEffect(sortField.value, sortOrder.value, userLocation.value) {
        gridState.scrollToItem(0)
    }
    // Refresh state
    val isRefreshing = stationsResource.value is Resource.Loading
    
    val snackbarHostState = remember { SnackbarHostState() }

    // Success Observer
    RefreshSuccessObserver(
        isRefreshing = isRefreshing,
        resource = stationsResource.value,
        snackbarHostState = snackbarHostState
    )

    // Filter logic
    val displayedFavorites = remember(favoriteStations.value, bookmarkSearchQuery) {
        if (bookmarkSearchQuery.trim().isNotEmpty()) {
            val q = bookmarkSearchQuery.trim().lowercase()
            favoriteStations.value.filter { station ->
                station.name.lowercase().contains(q) || station.address.lowercase().contains(q)
            }
        } else {
            favoriteStations.value
        }
    }

    Box(modifier = modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {
        // App Bar
        if (!isSearching) {
            TopAppBar(
                title = { Text("Favorites") },
                windowInsets = WindowInsets(top = 0.dp),
                actions = {
                    IconButton(onClick = { showSortMenu = !showSortMenu }) {
                        Icon(Icons.Filled.SortByAlpha, contentDescription = "Tri")
                    }

                    SortMenu(
                        isMenuOpen = showSortMenu,
                        onMenuOpenChange = { showSortMenu = it },
                        viewModel = viewModel,
                        modifier = Modifier.padding(top = 32.dp)
                    )

                    IconButton(onClick = { isSearching = !isSearching }) {
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
                modifier = Modifier.background(Color(0xFF00999d))
            )
        }

        // Main Content with RateLimitedPullToRefresh
        RateLimitedPullToRefresh(
            isRefreshing = isRefreshing,
            onRefresh = { viewModel.loadStations() },
            snackbarHostState = snackbarHostState,
            modifier = Modifier.weight(1f)
        ) {
            if (displayedFavorites.isEmpty()) {
                // Empty State
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(top = 0.dp, bottom = 16.dp, start = 16.dp, end = 16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = if (bookmarkSearchQuery.isNotEmpty()) "Aucun résultat" else "Aucun favori",
                            style = MaterialTheme.typography.headlineSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        if (bookmarkSearchQuery.isEmpty()) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Ajoutez des stations favorites depuis la recherche pour les voir ici.",
                                style = MaterialTheme.typography.bodyMedium,
                                textAlign = TextAlign.Center,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            } else {
                // Dashboard Grid
                LazyVerticalGrid(
                    columns = GridCells.Adaptive(minSize = 140.dp),
                    contentPadding = PaddingValues(top = 0.dp, start = 16.dp, end = 16.dp, bottom = 80.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxSize(),
                    state = gridState
                ) {
                    items(
                        items = displayedFavorites,
                        key = { it.number },
                        span = { station ->
                            val isLarge = largeTileStations.value.contains(station.number)
                            GridItemSpan(if (isLarge) 2 else 1)
                        }
                    ) { station ->
                        FavoriteStationTile(
                            station = station,
                            isLarge = largeTileStations.value.contains(station.number),
                            onClick = { selectedStation = station },
                            onUnfavorite = { viewModel.toggleFavorite(station.number) },
                            onToggleSize = { viewModel.toggleStationSize(station.number) }
                        )
                    }
                }
            }
        }
    }

    LocationFab(
        viewModel = viewModel,
        snackbarHostState = snackbarHostState,
        modifier = Modifier
            .align(Alignment.BottomEnd)
            .padding(bottom = 16.dp, end = 16.dp)
    )

    SnackbarHost(
        hostState = snackbarHostState,
        modifier = Modifier
            .align(Alignment.BottomCenter)
            .padding(16.dp)
    )
    }

    // Station Details Bundle
    selectedStation?.let { station ->
        StationDetailsSheet(
            station = station,
            onDismiss = { selectedStation = null },
            onToggleFavorite = { stationNumber -> viewModel.toggleFavorite(stationNumber) }
        )
    }
}
