package xyz.doocode.velotoile.ui.components.details

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AssistChip
import androidx.compose.material3.ChipColors
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import xyz.doocode.velotoile.core.dto.Station

@Composable
fun StationDetailsHeader(
    station: Station,
    onBackClick: () -> Unit = {},
    onMapsClick: () -> Unit = {}
) {
    val hasWarning = station.status == "CLOSED" || station.overflow || !station.connected;
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = Color(0xFFB7007A),
            )
    ) {
        // Top row with back button, station number and maps button
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp)
                .padding(top = 8.dp),
            horizontalArrangement = androidx.compose.foundation.layout.Arrangement.SpaceBetween,
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

            IconButton(onClick = onMapsClick) {
                Icon(
                    imageVector = Icons.Filled.LocationOn,
                    contentDescription = "Ouvrir sur la carte",
                    tint = Color.White
                )
            }
        }

        // Station name (extract from "number - name" format if present)
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .padding(bottom = if (hasWarning) 0.dp else 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            val stationName = extractStationName(station.name)
            Text(
                text = stationName,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                textAlign = TextAlign.Center
            )
        }

        /*// Address (clickable)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onMapsClick() }
                .padding(horizontal = 16.dp, vertical = 1.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Filled.LocationOn,
                contentDescription = "Adresse",
                tint = Color.White,
                modifier = Modifier
                    .size(24.dp)
                    .padding(end = 8.dp)
            )
            Text(
                text = station.address,
                style = MaterialTheme.typography.bodySmall,
                color = Color.White.copy(alpha = 0.9f)
            )
        }*/

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
                imageVector = Icons.Filled.Warning,
                contentDescription = label,
                modifier = Modifier.padding(end = 4.dp),
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
