package xyz.doocode.velotoile.ui.screen

import androidx.compose.foundation.background
import xyz.doocode.velotoile.ui.theme.VelotoileTheme
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.SortByAlpha
import androidx.compose.material.icons.filled.MyLocation
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
import androidx.compose.ui.platform.LocalContext
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.LocationManager
import androidx.compose.foundation.layout.PaddingValues
import androidx.core.content.ContextCompat
import kotlinx.coroutines.launch
import xyz.doocode.velotoile.ui.viewmodel.StationsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(viewModel: StationsViewModel, modifier: Modifier = Modifier) {
    var isSearching by remember { mutableStateOf(false) }
    var showSortMenu by remember { mutableStateOf(false) }
    var selectedStation by remember { mutableStateOf<Station?>(null) }
    
    val filteredStations = viewModel.filteredStations.observeAsState(emptyList())
    val searchQuery = viewModel.searchQuery.observeAsState("")
    
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

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

        val stationsResource = viewModel.stations.observeAsState()
        val currentSortField = viewModel.sortField.observeAsState(SortField.NUMBER)
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
                    contentPadding = PaddingValues(bottom = 80.dp)
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

    // Station details sheet
    StationDetailsSheet(
        station = selectedStation,
        onDismiss = { selectedStation = null },
        onToggleFavorite = { stationNumber -> viewModel.toggleFavorite(stationNumber) }
    )
}
