package xyz.doocode.velotoile.ui.components.common

import Resource
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RateLimitedPullToRefresh(
    isRefreshing: Boolean,
    onRefresh: () -> Unit,
    snackbarHostState: SnackbarHostState,
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit
) {
    var lastRefreshTime by rememberSaveable { mutableLongStateOf(0L) }
    val scope = rememberCoroutineScope()

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
                onRefresh()
            }
        },
        modifier = modifier,
        content = content
    )
}

@Composable
fun RefreshSuccessObserver(
    isRefreshing: Boolean,
    resource: Resource<*>?,
    snackbarHostState: SnackbarHostState
) {
    var wasRefreshing by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    
    LaunchedEffect(isRefreshing) {
        if (wasRefreshing && !isRefreshing) {
            if (resource is Resource.Success<*>) {
                val time = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())
                scope.launch {
                    snackbarHostState.showSnackbar("Mis à jour à l'instant ($time)")
                }
            }
        }
        wasRefreshing = isRefreshing
    }
}
