package xyz.doocode.velotoile.ui.components.details

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ElectricBike
import androidx.compose.material.icons.filled.LocalParking
import androidx.compose.material.icons.filled.PedalBike
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import xyz.doocode.velotoile.core.dto.Station
import xyz.doocode.velotoile.ui.theme.*

@Composable
fun StationDetailsRecap(station: Station) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Mechanical bikes tile
        RecapTile(
            icon = Icons.Filled.PedalBike,
            label = "Vélos\nmécanique",
            value = station.totalStands.availabilities.mechanicalBikes.toString(),
            backgroundColor = MechanicalBikeBlue,
            modifier = Modifier.weight(1f)
        )

        // Electric bikes tile
        RecapTile(
            icon = Icons.Filled.ElectricBike,
            label = "Vélos\nélectrique",
            value = station.totalStands.availabilities.electricalBikes.toString(),
            backgroundColor = ElectricBikeGreen,
            modifier = Modifier.weight(1f)
        )

        // Available stands tile
        RecapTile(
            icon = Icons.Filled.LocalParking,
            label = "Places\ndispo",
            value = station.totalStands.availabilities.stands.toString(),
            backgroundColor = AvailableStandsYellow,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun RecapTile(
    icon: ImageVector,
    label: String,
    value: String,
    backgroundColor: Color,
    modifier: Modifier = Modifier
) {
    val isZero = value.toIntOrNull() == 0
    val finalBackgroundColor = if (isZero) MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f) else backgroundColor
    val finalContentColor = if (isZero) Color(0xFFD32F2F) else MaterialTheme.colorScheme.onSurface
    
    Column(
        modifier = modifier
            .background(
                color = finalBackgroundColor,
                shape = RoundedCornerShape(18.dp)
            )
            .padding(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = finalContentColor,
            modifier = Modifier.size(34.dp)
        )

        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = finalContentColor,
            fontWeight = FontWeight.Medium,
            textAlign = TextAlign.Center
        )

        Text(
            text = value,
            style = MaterialTheme.typography.headlineSmall,
            color = finalContentColor,
            fontWeight = FontWeight.Bold
        )
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
private fun RecapTilePreview() {
    VelotoileTheme {
        RecapTile(
            icon = Icons.Filled.PedalBike,
            label = "Vélos\nmécanique",
            value = "10",
            backgroundColor = MechanicalBikeBlue
        )
    }
}