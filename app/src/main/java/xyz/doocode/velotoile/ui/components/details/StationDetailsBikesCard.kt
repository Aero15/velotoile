package xyz.doocode.velotoile.ui.components.details

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.DirectionsBike
import androidx.compose.material.icons.filled.Eject
import androidx.compose.material.icons.filled.ElectricBike
import androidx.compose.material.icons.filled.PedalBike
import androidx.compose.material.icons.filled.Power
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import xyz.doocode.velotoile.core.dto.Station
import xyz.doocode.velotoile.ui.theme.*

@Composable
fun StationDetailsBikesCard(station: Station) {
    StationDetailsSection(
        title = "Vélos disponibles (${station.totalStands.availabilities.bikes})",
        icon = Icons.AutoMirrored.Filled.DirectionsBike,
    ) {
        Column(modifier = Modifier.padding(0.dp)) {
            // Mechanical bikes
            BikeInfoRow(
                label = "Mécaniques",
                icon = Icons.Filled.PedalBike,
                value = station.totalStands.availabilities.mechanicalBikes,
                backgroundColor = MechanicalBikeBlue.copy(alpha = 0.5f),
            )

            // Electrical bikes
            BikeInfoRow(
                label = "Électriques",
                icon = Icons.Filled.ElectricBike,
                value = station.totalStands.availabilities.electricalBikes,
                backgroundColor = ElectricBikeGreen.copy(alpha = 0.5f),
                modifier = Modifier.padding(top = 1.dp),
            )

            if (station.contractName != "besancon") {
                // Internal battery
                BikeInfoRow(
                    indent = true,
                    icon = Icons.Filled.Power,
                    label = "Batterie interne",
                    value = station.totalStands.availabilities.electricalInternalBatteryBikes,
                    backgroundColor = Color(0xFFFFA500).copy(alpha = 0.5f),
                    modifier = Modifier.padding(top = 1.dp),
                )

                // Removable battery
                BikeInfoRow(
                    indent = true,
                    icon = Icons.Filled.Eject,
                    label = "Batterie amovible",
                    value = station.totalStands.availabilities.electricalRemovableBatteryBikes,
                    backgroundColor = Color(0xFF00AA00).copy(alpha = 0.5f),
                    modifier = Modifier.padding(top = 1.dp),
                )
            }
        }
    }
}

@Composable
private fun BikeInfoRow(
    label: String,
    value: Number,
    icon: ImageVector,
    backgroundColor: Color,
    indent: Boolean = false,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(backgroundColor)
            .padding(start = 18.dp)
            .padding(end = 8.dp)
            .padding(vertical = 8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = if (indent) 28.dp else 0.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                //tint = finalIconColor,
                modifier = Modifier
                    .padding(end = 8.dp)
                    .size(24.dp)
            )

            Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.weight(1f)
            )

            Box(
                modifier = Modifier
                    .width(40.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = value.toString(),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
            }
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
private fun BikeInfoRowPreview() {
    VelotoileTheme {
        BikeInfoRow(
            label = "Mécaniques",
            value = 10,
            icon = Icons.Filled.PedalBike,
            backgroundColor = Color(0xFFFFA500).copy(alpha = 0.1f),
        )
    }
}
