package xyz.doocode.velotoile.activity

import StationsViewModel
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Sort
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import xyz.doocode.velotoile.core.dto.Station
import xyz.doocode.velotoile.ui.components.SortMenu
import xyz.doocode.velotoile.ui.components.StationsList
import xyz.doocode.velotoile.ui.components.SearchBar
import xyz.doocode.velotoile.ui.components.details.StationDetailsSheet
import xyz.doocode.velotoile.ui.theme.VelotoileTheme

class MainActivity : ComponentActivity() {
    private val viewModel: StationsViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        viewModel.loadStations()
        //viewModel.startAutoRefresh()

        setContent {
            VelotoileTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    MainScreen(
                        viewModel = viewModel,
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(viewModel: StationsViewModel, modifier: Modifier = Modifier) {
    var isSearching by remember { mutableStateOf(false) }
    var showSortMenu by remember { mutableStateOf(false) }
    var selectedStation by remember { mutableStateOf<Station?>(null) }
    val context = LocalContext.current
    
    val filteredStations = viewModel.filteredStations.observeAsState(emptyList())
    val searchQuery = viewModel.searchQuery.observeAsState("")

    Column(modifier = modifier.fillMaxSize()) {
        if (!isSearching) {
            TopAppBar(
                title = { Text("Stations") },
                //containerColor = Color(0xFFb7007a),
                actions = {
                    IconButton(
                        onClick = { isSearching = !isSearching }
                    ) {
                        Icon(Icons.Filled.Search, contentDescription = "Recherche")
                    }
                    
                    /*IconButton(
                        onClick = {
                            Toast.makeText(context, "TODO: Filtres", Toast.LENGTH_SHORT).show()
                        }
                    ) {
                        Icon(Icons.Filled.FilterList, contentDescription = "Filtres")
                    }*/
                    
                    IconButton(
                        onClick = { showSortMenu = !showSortMenu }
                    ) {
                        Icon(Icons.Filled.Sort, contentDescription = "Tri")
                    }
                }
            )
        } else {
            SearchBar(
                searchQuery = searchQuery.value,
                onSearchQueryChanged = { viewModel.setSearchQuery(it) },
                onCloseSearch = { isSearching = false }
            )
        }

        SortMenu(
            isMenuOpen = showSortMenu,
            onMenuOpenChange = { showSortMenu = it },
            viewModel = viewModel
        )

        val stationsResource = viewModel.stations.observeAsState()
        when (val resource = stationsResource.value) {
            is Resource.Loading -> {
                // Afficher un loader
            }
            is Resource.Success -> {
                StationsList(
                    stations = filteredStations.value,
                    modifier = Modifier.fillMaxSize(),
                    onStationClick = { station -> selectedStation = station }
                )
            }
            is Resource.Error -> {
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
        onDismiss = { selectedStation = null }
    )
}