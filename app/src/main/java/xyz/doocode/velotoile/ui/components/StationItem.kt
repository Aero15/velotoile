package xyz.doocode.velotoile.ui.components

import android.content.res.Configuration
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
import androidx.compose.material.icons.automirrored.filled.DirectionsBike
import androidx.compose.material.icons.filled.DirectionsBike
import androidx.compose.material.icons.filled.LocalParking
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import xyz.doocode.velotoile.core.dto.Station
import xyz.doocode.velotoile.ui.theme.VelotoileTheme

@Composable
fun StationItem(
    station: Station,
    modifier: Modifier = Modifier,
    onStationClick: (Station) -> Unit = {}
) {
    val shape = RoundedCornerShape(16.dp)
    Card(
        modifier = modifier
            .clip(shape)
            .fillMaxWidth()
            .clickable { onStationClick(station) },
        //elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = shape,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Station name
            Text(
                text = station.name,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp)
            )

            // Bikes and stands info
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                StationInfoItem(
                    icon = Icons.AutoMirrored.Filled.DirectionsBike,
                    label = "Vélos",
                    value = station.totalStands.availabilities.bikes.toString(),
                    modifier = Modifier.weight(1f)
                )

                StationInfoItem(
                    icon = Icons.Filled.LocalParking,
                    label = "Places",
                    value = station.totalStands.availabilities.stands.toString(),
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun StationInfoItem(
    icon: ImageVector,
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    val finalIconColor = getIconColor(value.toInt())
    val finalContentColor = getTextColor(value.toInt())
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = finalIconColor,
            modifier = Modifier
                .padding(end = 8.dp)
                .size(32.dp)
        )

        Column {
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = finalContentColor
            )

            Text(
                text = value,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = finalContentColor
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
fun StationInfoItemPreview() {
    VelotoileTheme {
        StationInfoItem(
            icon = Icons.AutoMirrored.Filled.DirectionsBike,
            label = "Vélos",
            value = "10",
        )
    }
}

@Composable
private fun getIconColor(value: Int): Color {
    return when {
        value == 0 -> Color(0xFFD32F2F)
        value in 1..2 -> Color(0xFFFF9800)
        else -> MaterialTheme.colorScheme.primary
    }
}

@Composable
private fun getTextColor(value: Int): Color {
    return when {
        value == 0 -> Color(0xFFD32F2F)
        value in 1..2 -> Color(0xFFFF9800)
        else -> MaterialTheme.colorScheme.onSurface
    }
}
