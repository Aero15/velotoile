package xyz.doocode.velotoile.ui.components.details

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.PinDrop
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material.icons.filled.WifiOff
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import xyz.doocode.velotoile.core.dto.Station

@Composable
fun StationDetailsInfoCard(
    station: Station,
    onAddressClick: () -> Unit = {}
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier.padding(top = 16.dp)
    ) {

        // 1. ADRESSE
        if (station.address.isNotEmpty()) {
            Row(
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .fillMaxWidth()
                    .clickable(onClick = onAddressClick),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    color = MaterialTheme.colorScheme.tertiaryContainer,
                    shape = CircleShape,
                    modifier = Modifier.size(48.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Filled.PinDrop,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onTertiaryContainer
                        )
                    }
                }

                Spacer(modifier = Modifier.width(16.dp))

                Column {
                    Text(
                        text = "Adresse",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = station.address,
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }

        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))

        // 2. GRID FEATURE ICONS
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // STATUS (Ouvert/Fermé)
            val isClosed = station.status == "CLOSED"
            FeatureItem(
                icon = if (isClosed) Icons.Filled.Cancel else Icons.Filled.CheckCircle,
                label = if (isClosed) "Fermée" else "Ouverte",
                activeColor = MaterialTheme.colorScheme.primary,
                isActive = !isClosed,
                modifier = Modifier.weight(1f)
            )

            // CONNECTION
            val isConnected = station.connected
            FeatureItem(
                icon = if (isConnected) Icons.Filled.Wifi else Icons.Filled.WifiOff,
                label = if (isConnected) "En ligne" else "Hors ligne",
                activeColor = MaterialTheme.colorScheme.primary,
                isActive = isConnected,
                modifier = Modifier.weight(1f)
            )

            // CB / PAIEMENT
            FeatureItem(
                icon = Icons.Filled.CreditCard,
                label = "Carte Bancaire",
                activeColor = Color(0xFF0079C1),
                isActive = station.banking,
                modifier = Modifier.weight(1f)
            )

            // BONUS
            FeatureItem(
                icon = Icons.Filled.Star,
                label = "Bonus",
                activeColor = Color(0xFFFFB300),
                isActive = station.bonus,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun FeatureItem(
    icon: ImageVector,
    label: String,
    activeColor: Color,
    isActive: Boolean,
    modifier: Modifier = Modifier
) {
    val containerColor =
        if (isActive) activeColor.copy(alpha = 0.2f) else MaterialTheme.colorScheme.errorContainer
    val contentColor = if (isActive) activeColor else MaterialTheme.colorScheme.error

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Box(modifier = Modifier.size(56.dp)) {
            Surface(
                color = containerColor,
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = contentColor,
                        modifier = Modifier.size(28.dp)
                    )

                    if (!isActive) {
                        Canvas(modifier = Modifier.fillMaxSize()) {
                            val strokeWidth = 2.dp.toPx()
                            drawLine(
                                color = contentColor.copy(alpha = 0.5f),
                                start = Offset(10.dp.toPx(), size.height - 10.dp.toPx()),
                                end = Offset(size.width - 10.dp.toPx(), 10.dp.toPx()),
                                strokeWidth = strokeWidth
                            )
                        }
                    }
                }
            }

            if (isActive) {
                Surface(
                    color = Color(0xFF4CAF50), // Green for checkmark
                    shape = CircleShape,
                    modifier = Modifier
                        .size(20.dp)
                        .align(Alignment.TopEnd)
                        .offset(x = 4.dp, y = (-4).dp),
                    border = androidx.compose.foundation.BorderStroke(
                        2.dp,
                        MaterialTheme.colorScheme.surface
                    )
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Filled.Check,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(12.dp)
                        )
                    }
                }
            }
        }

        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = if (isActive) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.error,
            textAlign = TextAlign.Center,
            minLines = 2,
            maxLines = 2,
            modifier = Modifier.fillMaxWidth()
        )
    }
}
