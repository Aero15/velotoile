package xyz.doocode.velotoile.ui.components.common

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.LocationManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import kotlinx.coroutines.launch
import xyz.doocode.velotoile.ui.viewmodel.StationsViewModel

@Composable
fun LocationFab(
    viewModel: StationsViewModel,
    snackbarHostState: SnackbarHostState,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

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
                    scope.launch { 
                        snackbarHostState.currentSnackbarData?.dismiss()
                        snackbarHostState.showSnackbar("Tri par proximité activé") 
                    }
                } ?: run {
                     scope.launch { 
                        snackbarHostState.currentSnackbarData?.dismiss()
                        snackbarHostState.showSnackbar("Impossible de récupérer la position") 
                    }
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
             scope.launch { 
                snackbarHostState.currentSnackbarData?.dismiss()
                snackbarHostState.showSnackbar("Permission de localisation nécessaire") 
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
        modifier = modifier,
        containerColor = MaterialTheme.colorScheme.primaryContainer
    ) {
        Icon(Icons.Filled.MyLocation, contentDescription = "Localisation")
    }
}
