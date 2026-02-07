package xyz.doocode.velotoile.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.lazy.rememberLazyListState
import xyz.doocode.velotoile.ui.theme.VelotoileTheme
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.SortByAlpha
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import xyz.doocode.velotoile.ui.components.search.menu.SortMenu
import xyz.doocode.velotoile.ui.components.StationsList
import xyz.doocode.velotoile.ui.components.search.SearchBar
import xyz.doocode.velotoile.ui.components.details.StationDetailsSheet
import xyz.doocode.velotoile.core.dto.Station
import androidx.compose.foundation.layout.Box
import androidx.compose.ui.Alignment
import androidx.compose.foundation.layout.PaddingValues
import xyz.doocode.velotoile.ui.viewmodel.StationsViewModel
import xyz.doocode.velotoile.ui.components.common.LocationFab
import xyz.doocode.velotoile.ui.components.common.RateLimitedPullToRefresh
import xyz.doocode.velotoile.ui.components.common.RefreshSuccessObserver

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(viewModel: StationsViewModel, modifier: Modifier = Modifier) {
    var isSearching by remember { mutableStateOf(false) }
    var showSortMenu by remember { mutableStateOf(false) }
    var selectedStation by remember { mutableStateOf<Station?>(null) }
    
    val filteredStations = viewModel.filteredStations.observeAsState(emptyList())
    val searchQuery = viewModel.searchQuery.observeAsState("")
    val currentSortField = viewModel.sortField.observeAsState(SortField.NUMBER)
    val sortOrder = viewModel.sortOrder.observeAsState()
    val userLocation = viewModel.userLocation.observeAsState()
    val listState = rememberLazyListState()

    LaunchedEffect(currentSortField.value, sortOrder.value, userLocation.value) {
        listState.scrollToItem(0)
    }
    
    val snackbarHostState = remember { SnackbarHostState() }
    val stationsResource = viewModel.stations.observeAsState()
    
    RefreshSuccessObserver(
        isRefreshing = stationsResource.value is Resource.Loading,
        resource = stationsResource.value,
        snackbarHostState = snackbarHostState
    )

    Box(modifier = modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {
            if (!isSearching) {
            TopAppBar(
                modifier = Modifier.padding(0.dp),
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = VelotoileTheme.colors.topBarBackground,
                ),
                windowInsets = WindowInsets(top = 0.dp),
                title = { Text("Recherche") },
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
        
        RateLimitedPullToRefresh(
            isRefreshing = stationsResource.value is Resource.Loading,
            onRefresh = { viewModel.loadStations() },
            snackbarHostState = snackbarHostState,
            modifier = Modifier.weight(1f)
        ) {
            when (val resource = stationsResource.value) {
                is Resource.Loading<*> -> {
                    // Afficher un loader
                    Box(modifier = Modifier.fillMaxSize())
                }
                is Resource.Success<*> -> {
                    StationsList(
                        stations = filteredStations.value,
                        modifier = Modifier.fillMaxSize(),
                        onStationClick = { station -> selectedStation = station },
                        sortField = currentSortField.value,
                        isSearching = isSearching,
                        contentPadding = PaddingValues(bottom = 80.dp),
                        state = listState
                    )
                }
                is Resource.Error<*> -> {
                    // Afficher l'erreur
                }
                else -> {
                    // État initial
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

    // Station details sheet
    StationDetailsSheet(
        station = selectedStation,
        onDismiss = { selectedStation = null },
        onToggleFavorite = { stationNumber -> viewModel.toggleFavorite(stationNumber) }
    )
}
