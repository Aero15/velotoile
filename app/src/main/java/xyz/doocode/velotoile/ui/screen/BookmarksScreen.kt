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
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.ui.platform.LocalContext
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.LocationManager
import androidx.core.content.ContextCompat
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

    val context = LocalContext.current
    
    // Function to get location and sort
    fun refreshLocationAndSort() {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
            ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
        ) {
             try {
                val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
                val lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER) 
                    ?: locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
                
                lastKnownLocation?.let {
                    viewModel.updateUserLocation(it)
                    viewModel.setSortField(SortField.PROXIMITY)
                    scope.launch { snackbarHostState.showSnackbar("Tri par proximité activé") }
                } ?: run {
                     scope.launch { snackbarHostState.showSnackbar("Impossible de récupérer la position") }
                }
            } catch (e: Exception) {
                 e.printStackTrace()
            }
        }
    }

    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        if (permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true || 
            permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true) {
            refreshLocationAndSort()
        } else {
             scope.launch { snackbarHostState.showSnackbar("Permission de localisation nécessaire") }
        }
    }

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
                    contentPadding = PaddingValues(top = 16.dp, start = 16.dp, end = 16.dp, bottom = 80.dp),
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

    FloatingActionButton(
        onClick = {
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
            ) {
                refreshLocationAndSort()
            } else {
                locationPermissionLauncher.launch(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION))
            }
        },
        modifier = Modifier
            .align(Alignment.BottomEnd)
            .padding(bottom = 16.dp, end = 16.dp),
        containerColor = MaterialTheme.colorScheme.primaryContainer
    ) {
        Icon(Icons.Filled.MyLocation, contentDescription = "Localisation")
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
