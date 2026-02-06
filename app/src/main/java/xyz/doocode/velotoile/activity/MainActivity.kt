package xyz.doocode.velotoile.activity

import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
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
import xyz.doocode.velotoile.ui.viewmodel.StationsViewModel

class MainActivity : ComponentActivity() {
    private val viewModel: StationsViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        viewModel.initializePreferences(this)
        viewModel.loadStations()
        viewModel.startAutoRefresh()

        setContent {
            VelotoileTheme {
                NavBarApp(viewModel)
            }
        }
    }
}

//@PreviewScreenSizes
@Composable
fun NavBarApp(viewModel: StationsViewModel) {
    var currentDestination by rememberSaveable { mutableStateOf(AppDestinations.HOME) }

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
                        AppDestinations.SEARCH -> SearchScreen(viewModel = viewModel, modifier = Modifier.fillMaxSize())
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
    BOOKMARKS("Bookmarks", Icons.Rounded.FavoriteBorder, Icons.Filled.Favorite),
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