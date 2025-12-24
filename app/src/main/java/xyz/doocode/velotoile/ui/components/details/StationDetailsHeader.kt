package xyz.doocode.velotoile.ui.components.details

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.rounded.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.*
import xyz.doocode.velotoile.core.dto.Station
import xyz.doocode.velotoile.core.util.Preferences
import xyz.doocode.velotoile.ui.theme.VelotoileTheme

@Composable
fun StationDetailsHeader(
    station: Station,
    onBackClick: () -> Unit = {},
    onMapsClick: () -> Unit = {},
    onToggleFavorite: (Int) -> Unit = {}
) {
    val context = LocalContext.current
    val preferences = remember { Preferences(context) }
    val isFavorite = remember { mutableStateOf(preferences.isFavorite(station.number)) }
    
    val hasWarning = station.status == "CLOSED" || station.overflow || !station.connected;
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = Color(0xFFB7007A),
            )
    ) {
        // Top row with back button, station number and favorite button
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp)
                .padding(top = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBackClick) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Retour",
                    tint = Color.White
                )
            }

            // Station number (formatted) - centered
            Text(
                text = formatNumber(station.number),
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.SemiBold,
                color = Color.White.copy(alpha = 0.8f),
                modifier = Modifier.weight(1f),
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )

            IconButton(onClick = {
                // Use provided callback to toggle favorite in ViewModel / owner, then refresh local state
                onToggleFavorite(station.number)
                isFavorite.value = preferences.isFavorite(station.number)
            }) {
                Icon(
                    imageVector = if (isFavorite.value) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                    contentDescription = if (isFavorite.value) "Retirer des favoris" else "Ajouter aux favoris",
                    tint = Color.White
                )
            }
        }

        // Station name (extract from "number - name" format if present)
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .padding(bottom = if (hasWarning) 0.dp else 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            val stationName = extractStationName(station.name)
            Text(
                text = stationName,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                textAlign = TextAlign.Center,
                fontSize = 18.sp
            )
        }

        // Status and additional chips
        if (hasWarning) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (station.status == "CLOSED") {
                    StatusChip(label = "Fermée", modifier = Modifier.padding(end = 8.dp))
                }
                if (station.overflow) {
                    StatusChip(label = "Débordement", modifier = Modifier.padding(end = 8.dp))
                }
                if (!station.connected) {
                    StatusChip(label = "Hors ligne")
                }
            }
        }
    }
}

@Composable
private fun StatusChip(
    label: String,
    modifier: Modifier = Modifier
) {
    val primaryColor = Color(0xFFB7007A)
    AssistChip(
        onClick = { },
        label = {
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = primaryColor
            )
        },
        leadingIcon = {
            Icon(
                imageVector = Icons.Rounded.Warning,
                contentDescription = label,
                tint = primaryColor
            )
        },
        colors = ChipColors(
            containerColor = Color.White,
            labelColor = primaryColor,
            leadingIconContentColor = primaryColor,
            trailingIconContentColor = primaryColor,
            disabledContainerColor = Color.White,
            disabledLabelColor = primaryColor,
            disabledLeadingIconContentColor = primaryColor,
            disabledTrailingIconContentColor = primaryColor
        ),
        border = null,
        modifier = modifier
    )
}

@Preview(
    name = "Status chip",
)
@Composable
private fun StatusChipPreview() {
    VelotoileTheme {
        StatusChip(label = "Fermée")
    }
}

private fun extractStationName(fullName: String): String {
    // Format: "123 - Nom de la station" -> "Nom de la station"
    return if (fullName.contains(" - ")) {
        fullName.substringAfter(" - ").trim()
    } else {
        fullName
    }
}

private fun formatNumber(number: Int): String {
    // Format: 14068 -> "14 068"
    return number.toString().reversed().chunked(3).joinToString(" ").reversed()
}
