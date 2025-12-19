package xyz.doocode.velotoile.activity

import StationsViewModel
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import xyz.doocode.velotoile.ui.components.StationsList
import xyz.doocode.velotoile.ui.theme.VelotoileTheme

class MainActivity : ComponentActivity() {
    private val viewModel: StationsViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        viewModel.loadStations()

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

@Composable
fun MainScreen(viewModel: StationsViewModel, modifier: Modifier = Modifier) {
    val stationsResource = viewModel.stations.observeAsState()

    when (val resource = stationsResource.value) {
        is Resource.Loading -> {
            // Afficher un loader
        }
        is Resource.Success -> {
            resource.data?.let { stations ->
                StationsList(stations = stations, modifier = modifier)
            }
        }
        is Resource.Error -> {
            // Afficher l'erreur
        }
        else -> {
            // État initial
        }
    }
}