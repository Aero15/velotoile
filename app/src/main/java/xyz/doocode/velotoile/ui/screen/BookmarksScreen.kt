package xyz.doocode.velotoile.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.SortByAlpha
import androidx.compose.material3.*
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
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
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookmarksScreen(viewModel: StationsViewModel, modifier: Modifier = Modifier) {
    var isSearching by remember { mutableStateOf(false) }
    var showSortMenu by remember { mutableStateOf(false) }
    var selectedStation by remember { mutableStateOf<Station?>(null) }
    var bookmarkSearchQuery by rememberSaveable { mutableStateOf("") }

    val favoriteStations = viewModel.favoriteStations.observeAsState(emptyList())
    val stationsResource = viewModel.stations.observeAsState()
    
    // Refresh state
    val isRefreshing = stationsResource.value is Resource.Loading
    
    // Rate limiting & Snackbar state
    var lastRefreshTime by rememberSaveable { mutableLongStateOf(0L) }
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    // Watch for refresh completion to show success message
    var wasRefreshing by remember { mutableStateOf(false) }
    LaunchedEffect(isRefreshing) {
        if (wasRefreshing && !isRefreshing) {
            if (stationsResource.value is Resource.Success<*>) {
                val time = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())
                scope.launch {
                    snackbarHostState.showSnackbar("Mis à jour à l'instant ($time)")
                }
            }
        }
        wasRefreshing = isRefreshing
    }

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
                /*colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = VelotoileTheme.colors.topBarBackground,
                ),*/
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

        // Main Content with PullToRefresh
        PullToRefreshBox(
            isRefreshing = isRefreshing,
            onRefresh = {
                val currentTime = System.currentTimeMillis()
                if (currentTime - lastRefreshTime < 15_000) {
                    scope.launch {
                        snackbarHostState.currentSnackbarData?.dismiss()
                        snackbarHostState.showSnackbar("Veuillez réessayer ultérieurement.")
                    }
                } else {
                    lastRefreshTime = currentTime
                    viewModel.loadStations()
                }
            },
            modifier = Modifier.fillMaxSize()
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
                    contentPadding = PaddingValues(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(
                        items = displayedFavorites,
                        key = { it.number }
                    ) { station ->
                        FavoriteStationTile(
                            station = station,
                            onClick = { selectedStation = station },
                            onUnfavorite = { viewModel.toggleFavorite(station.number) }
                        )
                    }
                }
            }
        }
    }

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
