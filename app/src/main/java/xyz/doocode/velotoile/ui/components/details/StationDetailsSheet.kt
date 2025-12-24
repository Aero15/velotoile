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
import xyz.doocode.velotoile.core.util.openMapsIntent

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StationDetailsSheet(
    station: Station?,
    onDismiss: () -> Unit,
    sheetState: SheetState = rememberModalBottomSheetState(),
    onToggleFavorite: (Int) -> Unit = {}
) {
    if (station != null) {
        val context = LocalContext.current

        ModalBottomSheet(
            onDismissRequest = onDismiss,
            sheetState = sheetState,
            containerColor = MaterialTheme.colorScheme.surface,
            scrimColor = MaterialTheme.colorScheme.scrim.copy(alpha = 0.3f),
            dragHandle = {
                /*Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFFB7007A))
                        .padding(vertical = 0.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    BottomSheetDefaults.DragHandle()
                }*/
            }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(bottom = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Header with station info (includes back and maps buttons)
                StationDetailsHeader(
                    station = station,
                    onBackClick = onDismiss,
                    onMapsClick = { openMapsIntent(context, station) },
                    onToggleFavorite = onToggleFavorite
                )

                // Recap tiles (mechanical bikes, electric bikes, available stands)
                StationDetailsRecap(station = station)

                // Bikes section
                StationDetailsBikesCard(station = station)

                // Stands section
                StationDetailsStandsCard(station = station)

                // Additional info
                StationDetailsInfoCard(
                    station = station,
                    onAddressClick = { openMapsIntent(context, station) }
                )
            }
        }
    }
}