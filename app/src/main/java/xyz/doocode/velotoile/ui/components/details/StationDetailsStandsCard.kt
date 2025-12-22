package xyz.doocode.velotoile.ui.components.details

import android.content.res.Configuration
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocalParking
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import xyz.doocode.velotoile.core.dto.Station
import xyz.doocode.velotoile.ui.theme.VelotoileTheme

@Composable
fun StationDetailsStandsCard(station: Station) {
    StationDetailsSection(
        icon = Icons.Filled.LocalParking,
        title = "Places libres",
    ) {
        Row(
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .padding(bottom = 16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Left side: info rows
            Column(
                modifier = Modifier.weight(1f)
            ) {
                // Total stands
                StandInfoRow(
                    label = "Libres",
                    value = station.totalStands.availabilities.stands,
                    backgroundColor = MaterialTheme.colorScheme.primary,
                    textColor = MaterialTheme.colorScheme.onPrimary
                )

                // Occupées
                StandInfoRow(
                    label = "Occupées",
                    value = station.totalStands.availabilities.bikes,
                    backgroundColor = MaterialTheme.colorScheme.surfaceVariant,
                    modifier = Modifier.padding(top = 4.dp),
                    textColor = MaterialTheme.colorScheme.onSurfaceVariant
                )

                // Capacity
                StandInfoRow(
                    label = "Capacité",
                    value = station.totalStands.capacity,
                    backgroundColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.5f),
                    modifier = Modifier.padding(top = 4.dp),
                    textColor = MaterialTheme.colorScheme.onSecondaryContainer
                )
            }

            // Right side: pie chart
            Box(
                modifier = Modifier
                    .padding(start = 16.dp),
                contentAlignment = Alignment.Center
            ) {
                StandsPieChart(
                    available = station.totalStands.availabilities.stands,
                    capacity = station.totalStands.capacity,
                    modifier = Modifier.size(100.dp)
                )
            }
        }
    }
}

@Composable
private fun StandInfoRow(
    label: String,
    value: Number,
    backgroundColor: Color,
    modifier: Modifier = Modifier,
    textColor: Color = Color.Black
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(backgroundColor, RoundedCornerShape(8.dp))
            .padding(horizontal = 8.dp)
            .padding(vertical = 4.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium,
                color = textColor,
                modifier = Modifier.weight(1f)
            )
            Text(
                text = value.toString(),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = textColor
            )
        }
    }
}

@Preview(
    name = "[Light] Row of info",
    //showBackground = true
)
@Preview(
    name = "[Dark] Row of info",
    uiMode = Configuration.UI_MODE_NIGHT_YES
)
@Composable
private fun StandInfoRowPreview() {
    VelotoileTheme {
        StandInfoRow(
            label = "Libres",
            value = 6,
            backgroundColor = MaterialTheme.colorScheme.primary,
            textColor = MaterialTheme.colorScheme.onPrimary
        )
    }
}

@Composable
fun StandsPieChart(
    available: Int,
    capacity: Int,
    modifier: Modifier = Modifier
) {
    val availableAngle = (available.toFloat() / capacity.toFloat()) * 360f
    val usedAngle = 360f - availableAngle

    val availableColor = MaterialTheme.colorScheme.primary
    val usedColor = MaterialTheme.colorScheme.surfaceVariant

    Canvas(
        modifier = modifier.size(120.dp) // taille du camembert
    ) {
        // Places disponibles (vert)
        drawArc(
            color = availableColor,
            startAngle = -90f,
            sweepAngle = availableAngle,
            useCenter = true
        )

        // Places occupées (gris)
        drawArc(
            color = usedColor,
            startAngle = -90f + availableAngle,
            sweepAngle = usedAngle,
            useCenter = true
        )
    }
}

@Preview(
    name = "[Light] Pie chart",
    showBackground = true
)
@Preview(
    name = "[Dark] Pie chart",
    uiMode = Configuration.UI_MODE_NIGHT_YES
)
@Composable
private fun StandsPieChartPreview() {
    VelotoileTheme {
        StandsPieChart(
            available = 6,
            capacity = 20
        )
    }
}