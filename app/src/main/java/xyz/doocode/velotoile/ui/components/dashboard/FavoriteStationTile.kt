package xyz.doocode.velotoile.ui.components.dashboard

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.PedalBike
import androidx.compose.material.icons.filled.ElectricBike
import androidx.compose.material.icons.filled.LocalParking
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import xyz.doocode.velotoile.core.dto.Station

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun FavoriteStationTile(
    station: Station,
    onClick: () -> Unit,
    onUnfavorite: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showMenu by remember { mutableStateOf(false) }
    val haptics = LocalHapticFeedback.current
    val clipboardManager = LocalClipboardManager.current

    // Determine colors/status
    val isOpen = station.status == "OPEN"
    val bikes = station.mainStands.availabilities.bikes
    val mechBikes = station.mainStands.availabilities.mechanicalBikes
    val elecBikes = station.mainStands.availabilities.electricalBikes
    val stands = station.mainStands.availabilities.stands
    
    val backgroundColor = when {
        !isOpen -> Color(0xFF616161) // Grey for closed
        bikes == 0 -> Color(0xFFD32F2F) // Red for empty
        bikes < 3 -> Color(0xFFF57C00) // Orange for low
        else -> Color(0xFF388E3C) // Green for good
    }
    
    val containerColor = MaterialTheme.colorScheme.surfaceContainer
    
    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(160.dp) // Fixed height for tile look
            .clip(RoundedCornerShape(12.dp))
            .combinedClickable(
                onClick = onClick,
                onLongClick = {
                    haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                    showMenu = true
                }
            ),
        colors = CardDefaults.cardColors(containerColor = containerColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            
            // Status bar at top or background hint
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .background(backgroundColor)
                    .align(Alignment.TopCenter)
            )

            Column(
                modifier = Modifier
                    .padding(12.dp)
                    .fillMaxSize(),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                // Header: Name and Status
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Text(
                        text = formatStationName(station.name),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f)
                    )
                    
                    if (!isOpen) {
                        Surface(
                            color = Color.Red,
                            shape = RoundedCornerShape(4.dp),
                            modifier = Modifier.padding(start = 4.dp)
                        ) {
                            Text(
                                text = "FERMÉ",
                                color = Color.White,
                                style = MaterialTheme.typography.labelSmall,
                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                            )
                        }
                    }
                }

                // Center: Big Data
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    // Bikes Group
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        StationCounter(
                            count = mechBikes,
                            icon = Icons.Default.PedalBike,
                            contentDescription = "Méca"
                        )
                        StationCounter(
                            count = elecBikes,
                            icon = Icons.Filled.ElectricBike,
                            contentDescription = "Élec"
                        )
                    }

                    // Divider
                    Box(
                        modifier = Modifier
                            .width(1.dp)
                            .height(40.dp)
                            .background(MaterialTheme.colorScheme.outlineVariant)
                    )

                    // Stands
                    StationCounter(
                        count = stands,
                        icon = Icons.Default.LocalParking,
                        contentDescription = "Places"
                    )
                }
            }

            // Context Menu
            DropdownMenu(
                expanded = showMenu,
                onDismissRequest = { showMenu = false },
                offset = DpOffset(0.dp, 0.dp)
            ) {
                DropdownMenuItem(
                    text = { Text("Voir détails") },
                    onClick = {
                        showMenu = false
                        onClick()
                    },
                    leadingIcon = { Icon(Icons.Default.Info, contentDescription = null) }
                )
                DropdownMenuItem(
                    text = { Text("Copier l'adresse") },
                    onClick = {
                        clipboardManager.setText(AnnotatedString(station.address))
                        showMenu = false
                    },
                    leadingIcon = { Icon(Icons.Default.ContentCopy, contentDescription = null) }
                )
                HorizontalDivider()
                DropdownMenuItem(
                    text = { Text("Retirer des favoris") },
                    onClick = {
                        showMenu = false
                        onUnfavorite()
                    },
                    leadingIcon = { Icon(Icons.Default.Delete, contentDescription = null) },
                    colors = MenuDefaults.itemColors(textColor = MaterialTheme.colorScheme.error)
                )
            }
        }
    }
}

// Helper to clean station name (often has "NUMBER - NAME")
fun formatStationName(originalName: String): String {
    // Regex to remove leading numbers indicating station ID like "123 - "
    return originalName.replace(Regex("^\\d+\\s*-\\s*"), "")
}

@Composable
private fun StationCounter(
    count: Int,
    icon: ImageVector,
    contentDescription: String,
) {
    val iconColor = when (count) {
        0 -> Color(0xFFD32F2F)
        in 1..2 -> Color(0xFFFF9800)
        else -> MaterialTheme.colorScheme.primary
    }
    val textColor = when (count) {
        0 -> Color(0xFFD32F2F)
        in 1..2 -> Color(0xFFFF9800)
        else -> MaterialTheme.colorScheme.onSurface
    }

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            tint = iconColor,
            modifier = Modifier.size(24.dp)
        )
        Text(
            text = "$count",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = textColor
        )
    }
}
