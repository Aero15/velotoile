package xyz.doocode.velotoile.ui.components.details

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.DirectionsBike
import androidx.compose.material.icons.filled.DirectionsBike
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
fun StationDetailsBikesCard(station: Station) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.5f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Title
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.DirectionsBike,
                    contentDescription = "Vélos",
                    tint = MaterialTheme.colorScheme.secondary,
                    modifier = Modifier.padding(end = 8.dp)
                )
                Text(
                    text = "Vélos disponibles",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            // Total bikes
            BikeInfoRow(
                label = "Total",
                value = station.totalStands.availabilities.bikes.toString(),
                backgroundColor = MaterialTheme.colorScheme.secondary.copy(alpha = 0.1f)
            )

            // Mechanical bikes
            BikeInfoRow(
                label = "Mécaniques",
                value = station.totalStands.availabilities.mechanicalBikes.toString(),
                backgroundColor = Color(0xFFFFA500).copy(alpha = 0.1f),
                modifier = Modifier.padding(top = 8.dp)
            )

            // Electrical bikes
            BikeInfoRow(
                label = "Électriques",
                value = station.totalStands.availabilities.electricalBikes.toString(),
                backgroundColor = Color(0xFF00AA00).copy(alpha = 0.1f),
                modifier = Modifier.padding(top = 8.dp)
            )

            // Internal battery
            BikeInfoRow(
                label = "  ├─ Batterie interne",
                value = station.totalStands.availabilities.electricalInternalBatteryBikes.toString(),
                backgroundColor = Color(0xFF87CEEB).copy(alpha = 0.1f),
                modifier = Modifier.padding(top = 8.dp, start = 16.dp)
            )

            // Removable battery
            BikeInfoRow(
                label = "  └─ Batterie amovible",
                value = station.totalStands.availabilities.electricalRemovableBatteryBikes.toString(),
                backgroundColor = Color(0xFFD4AF37).copy(alpha = 0.1f),
                modifier = Modifier.padding(top = 8.dp, start = 16.dp)
            )
        }
    }
}

@Composable
private fun BikeInfoRow(
    label: String,
    value: String,
    backgroundColor: Color,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(backgroundColor, RoundedCornerShape(8.dp))
            .padding(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.weight(1f)
            )
            Text(
                text = value,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
