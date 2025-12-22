package xyz.doocode.velotoile.ui.components.search.result

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.DirectionsBike
import androidx.compose.material.icons.filled.ElectricBike
import androidx.compose.material.icons.filled.LocalParking
import androidx.compose.material.icons.filled.PedalBike
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import xyz.doocode.velotoile.core.dto.*
import xyz.doocode.velotoile.ui.theme.VelotoileTheme

@Composable
fun StationItem(
    station: Station,
    modifier: Modifier = Modifier,
    onStationClick: (Station) -> Unit = {},
    showMechanicalBikes: Boolean = false,
    showElectricalBikes: Boolean = false,
) {
    
    val shape = RoundedCornerShape(0.dp)
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onStationClick(station) },
        shape = shape,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.background
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .padding(top = 12.dp)
        ) {
            // Station name
            Text(
                text = station.name,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 4.dp)
            )

            // Bikes and stands info
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                StationInfoItem(
                    icon = Icons.AutoMirrored.Filled.DirectionsBike,
                    label = "Vélos",
                    value = station.totalStands.availabilities.bikes.toString(),
                    modifier = Modifier.weight(1f)
                )

                if (showMechanicalBikes) {
                    StationInfoItem(
                        icon = Icons.Filled.PedalBike,
                        label = "Méca",
                        value = station.totalStands.availabilities.mechanicalBikes.toString(),
                        modifier = Modifier.weight(1f)
                    )
                }

                if (showElectricalBikes) {
                    StationInfoItem(
                        icon = Icons.Filled.ElectricBike,
                        label = "Élec",
                        value = station.totalStands.availabilities.electricalBikes.toString(),
                        modifier = Modifier.weight(1f)
                    )
                }

                StationInfoItem(
                    icon = Icons.Filled.LocalParking,
                    label = "Places",
                    value = station.totalStands.availabilities.stands.toString(),
                    modifier = Modifier.weight(1f)
                )
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(MaterialTheme.colorScheme.outlineVariant)
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
private fun StationItemPreview() {
    val stands = Stands(
        capacity = 20,
        availabilities = Availabilities (
            bikes = 6,
            stands = 15,
            mechanicalBikes = 2,
            electricalBikes = 4,
            electricalInternalBatteryBikes = 3,
            electricalRemovableBatteryBikes = 1
        )
    )

    val station = Station(
        number = 1234,
        name = "Station de test",
        address = "123 Rue de Test, 69000 Lyon",
        position = Position(45.75, 4.85),
        lastUpdate = 1,
        banking = true,
        bonus = false,
        status = "OPEN",
        overflow = false,
        connected = true,
        contractName = "lyon",
        totalStands = stands,
        mainStands = stands
    )

    VelotoileTheme {
        StationItem(
            station = station,
            showMechanicalBikes = true,
            showElectricalBikes = true,
        )
    }
}
