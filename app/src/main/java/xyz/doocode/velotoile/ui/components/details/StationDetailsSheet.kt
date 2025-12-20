package xyz.doocode.velotoile.ui.components.details

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import xyz.doocode.velotoile.core.dto.Station

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StationDetailsSheet(
    station: Station?,
    onDismiss: () -> Unit,
    sheetState: SheetState = rememberModalBottomSheetState()
) {
    if (station != null) {
        val context = LocalContext.current

        ModalBottomSheet(
            onDismissRequest = onDismiss,
            sheetState = sheetState,
            containerColor = MaterialTheme.colorScheme.surface,
            scrimColor = MaterialTheme.colorScheme.scrim.copy(alpha = 0.3f),
            dragHandle = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFFB7007A)),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    BottomSheetDefaults.DragHandle()
                }
            }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(bottom = 32.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Header with station info (includes back and maps buttons)
                StationDetailsHeader(
                    station = station,
                    onBackClick = onDismiss,
                    onMapsClick = { openMapsIntent(context, station) }
                )

                // Bikes section
                StationDetailsBikesCard(station = station)

                // Stands section
                StationDetailsStandsCard(station = station)

                // Additional info
                StationDetailsInfoCard(station = station)
            }
        }
    }
}

private fun openMapsIntent(context: Context, station: Station) {
    val latitude = station.position.latitude
    val longitude = station.position.longitude
    val stationName = station.name

    // Format: geo:latitude,longitude?q=latitude,longitude(name)
    val geoUri = "geo:$latitude,$longitude?q=$latitude,$longitude($stationName)"
    
    try {
        val intent = Intent(Intent.ACTION_VIEW).apply {
            data = Uri.parse(geoUri)
        }
        context.startActivity(intent)
    } catch (e: Exception) {
        // Si aucune application de carte n'est disponible
        try {
            // Fallback vers Google Maps
            val mapsUri = "https://maps.google.com/maps?q=$latitude,$longitude"
            val webIntent = Intent(Intent.ACTION_VIEW, Uri.parse(mapsUri))
            context.startActivity(webIntent)
        } catch (e: Exception) {
            // Silencieusement ignorer l'erreur
        }
    }
}
