package xyz.doocode.velotoile.activity

import Resource
import xyz.doocode.velotoile.ui.viewmodel.StationsViewModel
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.compose.foundation.background
import xyz.doocode.velotoile.ui.theme.VelotoileTheme
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.SortByAlpha
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.FavoriteBorder
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import xyz.doocode.velotoile.core.dto.Station
import xyz.doocode.velotoile.ui.components.search.menu.SortMenu
import xyz.doocode.velotoile.ui.components.StationsList
import xyz.doocode.velotoile.ui.components.search.SearchBar
import xyz.doocode.velotoile.ui.components.details.StationDetailsSheet

class SearchActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Redirect to MainActivity (Search is now in MainActivity)
        startActivity(android.content.Intent(this, MainActivity::class.java))
        finish()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(viewModel: StationsViewModel, modifier: Modifier = Modifier) {
    var isSearching by remember { mutableStateOf(false) }
    var showSortMenu by remember { mutableStateOf(false) }
    var selectedStation by remember { mutableStateOf<Station?>(null) }
    
    val filteredStations = viewModel.filteredStations.observeAsState(emptyList())
    val searchQuery = viewModel.searchQuery.observeAsState("")
    val showOnlyFavorites = viewModel.showOnlyFavorites.observeAsState(false)

    Column(modifier = modifier.fillMaxSize()) {
        if (!isSearching) {
            TopAppBar(
                modifier = Modifier.padding(0.dp),
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = VelotoileTheme.colors.topBarBackground,
                ),
                windowInsets = WindowInsets(top = 0.dp),
                title = { Text("Ginko Vélocité") },
                actions = {
                    // Bouton pour filtrer les favoris
                    IconButton(
                        onClick = { viewModel.toggleFavoritesFilter() }
                    ) {
                        Icon(
                            imageVector = if (showOnlyFavorites.value) Icons.Rounded.Favorite else Icons.Rounded.FavoriteBorder,
                            contentDescription = if (showOnlyFavorites.value) "Voir toutes les stations" else "Afficher les favoris"
                        )
                    }
                    
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
            }
            is Resource.Success<*> -> {
                StationsList(
                    stations = filteredStations.value,
                    modifier = Modifier.fillMaxSize(),
                    onStationClick = { station -> selectedStation = station },
                    sortField = currentSortField.value,
                    isSearching = isSearching
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

    // Station details sheet
    StationDetailsSheet(
        station = selectedStation,
        onDismiss = { selectedStation = null },
        onToggleFavorite = { stationNumber -> viewModel.toggleFavorite(stationNumber) }
    )
}