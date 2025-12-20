package xyz.doocode.velotoile.ui.components.details

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AssistChip
import androidx.compose.material3.ChipColors
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import xyz.doocode.velotoile.core.dto.Station

@Composable
fun StationDetailsHeader(station: Station) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = Color(0xFFB7007A),
                shape = RoundedCornerShape(bottomStart = 16.dp, bottomEnd = 16.dp)
            )
            .padding(horizontal = 16.dp)
    ) {
        // Station number (formatted)
        Text(
            text = formatNumber(station.number),
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.SemiBold,
            color = Color.White.copy(alpha = 0.8f),
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 4.dp)
        )

        // Station name (extract from "number - name" format if present)
        val stationName = extractStationName(station.name)
        Text(
            text = stationName,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp)
        )

        // Address
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Filled.LocationOn,
                contentDescription = "Adresse",
                tint = Color.White,
                modifier = Modifier.padding(end = 8.dp)
            )
            Text(
                text = station.address,
                style = MaterialTheme.typography.bodySmall,
                color = Color.White.copy(alpha = 0.9f)
            )
        }

        // Status and additional chips
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Status chip - only display if closed
            if (station.status == "CLOSED") {
                AssistChip(
                    onClick = { },
                    label = {
                        Text(
                            text = "Fermée",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color(0xFFB7007A)
                        )
                    },
                    colors = ChipColors(
                        containerColor = Color.White,
                        labelColor = Color(0xFFB7007A),
                        leadingIconContentColor = Color(0xFFB7007A),
                        trailingIconContentColor = Color(0xFFB7007A),
                        disabledContainerColor = Color.White,
                        disabledLabelColor = Color(0xFFB7007A),
                        disabledLeadingIconContentColor = Color(0xFFB7007A),
                        disabledTrailingIconContentColor = Color(0xFFB7007A)
                    ),
                    border = null,
                    modifier = Modifier.padding(end = 8.dp)
                )
            }

            // Overflow chip
            if (station.overflow) {
                AssistChip(
                    onClick = { },
                    label = { 
                        Text(
                            "Débordement",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color(0xFFB7007A)
                        )
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Filled.Warning,
                            contentDescription = "Débordement",
                            modifier = Modifier.padding(end = 4.dp),
                            tint = Color(0xFFB7007A)
                        )
                    },
                    colors = ChipColors(
                        containerColor = Color.White,
                        labelColor = Color(0xFFB7007A),
                        leadingIconContentColor = Color(0xFFB7007A),
                        trailingIconContentColor = Color(0xFFB7007A),
                        disabledContainerColor = Color.White,
                        disabledLabelColor = Color(0xFFB7007A),
                        disabledLeadingIconContentColor = Color(0xFFB7007A),
                        disabledTrailingIconContentColor = Color(0xFFB7007A)
                    ),
                    border = null,
                    modifier = Modifier.padding(end = 8.dp)
                )
            }

            // Connected chip - only display if not connected
            if (!station.connected) {
                AssistChip(
                    onClick = { },
                    label = {
                        Text(
                            text = "Hors ligne",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color(0xFFB7007A)
                        )
                    },
                    colors = ChipColors(
                        containerColor = Color.White,
                        labelColor = Color(0xFFB7007A),
                        leadingIconContentColor = Color(0xFFB7007A),
                        trailingIconContentColor = Color(0xFFB7007A),
                        disabledContainerColor = Color.White,
                        disabledLabelColor = Color(0xFFB7007A),
                        disabledLeadingIconContentColor = Color(0xFFB7007A),
                        disabledTrailingIconContentColor = Color(0xFFB7007A)
                    ),
                    border = null
                )
            }
        }
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
