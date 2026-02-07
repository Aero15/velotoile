package xyz.doocode.velotoile.activity

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.content.ContextCompat
import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.rounded.FavoriteBorder
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteDefaults
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewScreenSizes
import androidx.compose.ui.unit.*
import xyz.doocode.velotoile.ui.screen.BookmarksScreen
import xyz.doocode.velotoile.ui.screen.SearchScreen
import xyz.doocode.velotoile.ui.theme.VelotoileTheme
import androidx.activity.viewModels
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.rounded.Menu
import androidx.compose.material.icons.rounded.Search
import androidx.compose.runtime.remember
import xyz.doocode.velotoile.ui.viewmodel.StationsViewModel
import SortField

class MainActivity : ComponentActivity() {
    private val viewModel: StationsViewModel by viewModels()
    private var locationManager: LocationManager? = null

    private val locationListener = object : LocationListener {
        override fun onLocationChanged(location: Location) {
            viewModel.updateUserLocation(location)
        }

        override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {}
        override fun onProviderEnabled(provider: String) {}
        override fun onProviderDisabled(provider: String) {}
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        viewModel.initializePreferences(this)

        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager

        viewModel.sortField.observe(this) { sortField ->
            if (sortField == SortField.PROXIMITY) {
                startLocationUpdates()
            } else {
                stopLocationUpdates()
            }
        }

        setContent {
            VelotoileTheme {
                NavBarApp(viewModel)
            }
        }
    }

    override fun onStart() {
        super.onStart()
        viewModel.loadStations()
        viewModel.startAutoRefresh()
        if (viewModel.sortField.value == SortField.PROXIMITY) {
            startLocationUpdates()
        }
    }

    override fun onStop() {
        super.onStop()
        viewModel.stopAutoRefresh()
        stopLocationUpdates()
    }

    private fun startLocationUpdates() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            try {
                locationManager?.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER,
                    2000L, // 2 seconds
                    5f,    // 5 meters
                    locationListener
                )
                // Also listen to network provider for faster location
                locationManager?.requestLocationUpdates(
                    LocationManager.NETWORK_PROVIDER,
                    5000L,
                    10f,
                    locationListener
                )
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun stopLocationUpdates() {
        locationManager?.removeUpdates(locationListener)
    }
}

//@PreviewScreenSizes
@Composable
fun NavBarApp(viewModel: StationsViewModel) {
    val initialScreenName = remember { viewModel.getLastScreen() }
    val initialDestination = remember(initialScreenName) {
        try {
            AppDestinations.valueOf(initialScreenName)
        } catch (e: Exception) {
            AppDestinations.HOME
        }
    }

    var currentDestination by rememberSaveable { mutableStateOf(initialDestination) }

    NavigationSuiteScaffold(
        navigationSuiteItems = {
            AppDestinations.entries.forEach {
                item(
                    icon = {
                        Icon(
                            if (it == currentDestination) it.selectedIcon else it.icon,
                            contentDescription = it.label
                        )
                    },
                    label = { Text(it.label) },
                    selected = it == currentDestination,
                    onClick = {
                        currentDestination = it
                        viewModel.setLastScreen(it.name)
                    }
                )
            }
        },
        navigationSuiteColors = NavigationSuiteDefaults.colors(
            navigationBarContainerColor = Color.Transparent,
        )
    ) {
        Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
            Box(modifier = Modifier.padding(innerPadding)) {
                AnimatedContent(
                    targetState = currentDestination,
                    transitionSpec = {
                        val direction = if (targetState.ordinal > initialState.ordinal) 1 else -1
                        slideInHorizontally(
                            animationSpec = tween(durationMillis = 250),
                            initialOffsetX = { it * direction }
                        ) + fadeIn(animationSpec = tween(durationMillis = 100)) togetherWith
                                slideOutHorizontally(
                                    animationSpec = tween(durationMillis = 250),
                                    targetOffsetX = { -it * direction }
                                ) + fadeOut(animationSpec = tween(durationMillis = 100))
                    }
                ) { destination ->
                    when (destination) {
                        AppDestinations.HOME -> HomeScreen()
                        AppDestinations.SEARCH -> SearchScreen(
                            viewModel = viewModel,
                            modifier = Modifier.fillMaxSize()
                        )

                        AppDestinations.BOOKMARKS -> BookmarksScreen(viewModel = viewModel)
                        AppDestinations.MENU -> MenuScreen()
                    }
                }
            }
        }
    }
}

enum class AppDestinations(
    val label: String,
    val icon: ImageVector,
    val selectedIcon: ImageVector
) {
    HOME("Home", Icons.Outlined.Home, Icons.Filled.Home),
    SEARCH("Search", Icons.Rounded.Search, Icons.Filled.Search),
    BOOKMARKS("Favorites", Icons.Rounded.FavoriteBorder, Icons.Filled.Favorite),
    MENU("Menu", Icons.Rounded.Menu, Icons.Filled.Menu),
}

@Composable
fun HomeScreen() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color.Red)
    ) {
        Column(
            modifier = Modifier
                .align(alignment = Alignment.Center)
                .padding(16.dp)
                .fillMaxSize()
                .background(color = Color.Blue)
        ) {
            Text(
                "Velo",
                fontSize = 64.sp,
                modifier = Modifier
                    .background(color = Color.Magenta)
            )
        }
    }
}

@Preview(
    name = "Light mode",
    showBackground = true
)
@Preview(
    name = "Dark mode",
    uiMode = Configuration.UI_MODE_NIGHT_YES
)
@Composable
private fun HomeScreenPreview() {
    VelotoileTheme {
        HomeScreen()
    }
}

@Composable
fun MenuScreen() {
    Text("TODO: Menu")
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    VelotoileTheme {
        Greeting("Android")
    }
}