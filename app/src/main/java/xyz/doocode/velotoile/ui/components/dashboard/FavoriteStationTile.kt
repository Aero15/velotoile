package xyz.doocode.velotoile.ui.components.dashboard

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import xyz.doocode.velotoile.core.dto.Station
import SortField

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun FavoriteStationTile(
    station: Station,
    isLarge: Boolean = false,
    onClick: () -> Unit,
    onUnfavorite: () -> Unit,
    onToggleSize: () -> Unit,
    modifier: Modifier = Modifier,
    sortField: SortField = SortField.NUMBER
) {
    var showMenu by remember { mutableStateOf(false) }
    val haptics = LocalHapticFeedback.current
    val clipboardManager = LocalClipboardManager.current

    // Determine colors/status
    val isOpen = station.status == "OPEN"

    val targetValue = when (sortField) {
        SortField.AVAILABLE_STANDS -> station.mainStands.availabilities.stands
        SortField.MECHANICAL_BIKES -> station.mainStands.availabilities.mechanicalBikes
        SortField.ELECTRICAL_BIKES -> station.mainStands.availabilities.electricalBikes
        else -> station.mainStands.availabilities.bikes
    }

    val backgroundColor = when {
        !isOpen -> Color(0xFF616161)
        targetValue == 0 -> Color(0xFFD32F2F)
        targetValue < 3 -> Color(0xFFF57C00)
        else -> Color(0xFF388E3C)
    }

    val surfaceColor = MaterialTheme.colorScheme.surfaceContainer
    val topColor = backgroundColor.copy(alpha = 0.4f).compositeOver(surfaceColor)
    val bottomColor = backgroundColor.copy(alpha = 0.1f).compositeOver(surfaceColor)

    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(160.dp)
            .clip(RoundedCornerShape(12.dp))
            .combinedClickable(
                onClick = onClick,
                onLongClick = {
                    haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                    showMenu = true
                }
            ),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(topColor, bottomColor)
                    )
                )
        ) {
            // Status bar
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .background(backgroundColor)
                    .align(Alignment.TopCenter)
            )

            if (isLarge) {
                LargeStationContent(station, isOpen, sortField)
            } else {
                StandardStationContent(station, isOpen, sortField)
            }

            // Context Menu
            DropdownMenu(
                expanded = showMenu,
                onDismissRequest = { showMenu = false },
                offset = DpOffset(0.dp, 0.dp)
            ) {
                DropdownMenuItem(
                    text = { Text("Voir détails") },
                    onClick = { showMenu = false; onClick() },
                    leadingIcon = { Icon(Icons.Default.Info, contentDescription = null) }
                )
                DropdownMenuItem(
                    text = { Text(if (isLarge) "Taille normale" else "Grande taille") },
                    onClick = { showMenu = false; onToggleSize() },
                    leadingIcon = {
                        Icon(
                            if (isLarge) Icons.Default.CloseFullscreen else Icons.Default.OpenInFull,
                            contentDescription = null
                        )
                    }
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
                    onClick = { showMenu = false; onUnfavorite() },
                    leadingIcon = { Icon(Icons.Default.Delete, contentDescription = null) },
                    colors = MenuDefaults.itemColors(textColor = MaterialTheme.colorScheme.error)
                )
            }
        }
    }
}

@Composable
fun StandardStationContent(station: Station, isOpen: Boolean, sortField: SortField) {
    StationMainContent(
        station = station,
        isOpen = isOpen,
        isLarge = false,
        sortField = sortField,
        modifier = Modifier.padding(12.dp)
    )
}

@Composable
fun LargeStationContent(station: Station, isOpen: Boolean, sortField: SortField) {
    Row(modifier = Modifier.fillMaxSize()) {
        // LEFT COLUMN: Main Stats (Reusing common component)
        StationMainContent(
            station = station,
            isOpen = isOpen,
            isLarge = true,
            sortField = sortField,
            modifier = Modifier
                .weight(1f)
                .padding(12.dp)
        )

        // Vertical Divider
        Box(
            modifier = Modifier
                .width(1.dp)
                .fillMaxHeight()
                .padding(vertical = 12.dp)
                .background(MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
        )

        // RIGHT COLUMN: Extra Info
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(12.dp)
                .fillMaxHeight(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {

            // Header: Station Number & Status
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.Top
            ) {
                if (!isOpen) {
                    ClosedBadge()
                } else {
                    Text(
                        text = "#${station.number}",
                        style = MaterialTheme.typography.headlineLarge,
                        fontWeight = FontWeight.Black
                    )
                }
            }

            // Info Grid / List
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (station.bonus) InfoRow(Icons.Default.Star, "Bonus", Color(0xFFFFD700)) // Gold
                if (station.banking) InfoRow(
                    Icons.Default.CreditCard,
                    "TPE",
                    MaterialTheme.colorScheme.onSurface
                )
                if (station.overflow) InfoRow(Icons.Default.Warning, "Overflow", Color(0xFFFF9800))
                if (station.connected) InfoRow(
                    Icons.Default.Wifi,
                    "Connecté",
                    MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

@Composable
fun StationMainContent(
    station: Station,
    isOpen: Boolean,
    isLarge: Boolean,
    sortField: SortField,
    modifier: Modifier = Modifier
) {
    val mechBikes = station.mainStands.availabilities.mechanicalBikes
    val elecBikes = station.mainStands.availabilities.electricalBikes
    val stands = station.mainStands.availabilities.stands

    val isMechBold = sortField == SortField.MECHANICAL_BIKES || sortField == SortField.TOTAL_BIKES
    val isElecBold = sortField == SortField.ELECTRICAL_BIKES || sortField == SortField.TOTAL_BIKES
    val isStandsBold = sortField == SortField.AVAILABLE_STANDS

    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top
        ) {
            Text(
                text = formatStationName(station.name),
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f)
            )
            // In standard mode, we show the badge next to the title.
            // In large mode, the badge is moved to the right column.
            if (!isLarge && !isOpen) {
                ClosedBadge()
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            StationCounter(mechBikes, Icons.Default.PedalBike, "Méca", isLarge, isBold = isMechBold)
            StationCounter(
                elecBikes,
                Icons.Filled.ElectricBike,
                "Élec",
                isLarge,
                isBold = isElecBold
            )
            StationCounter(
                stands,
                Icons.Default.LocalParking,
                "Places",
                isLarge,
                isBold = isStandsBold
            )
        }
    }
}

@Composable
fun InfoRow(icon: ImageVector, text: String, tint: Color) {
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.End) {
        Text(
            text,
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.Medium,
            color = tint
        )
        Spacer(modifier = Modifier.width(8.dp))
        Icon(icon, contentDescription = null, tint = tint, modifier = Modifier.size(16.dp))
    }
}

@Composable
fun ClosedBadge() {
    Surface(
        color = MaterialTheme.colorScheme.error,
        shape = RoundedCornerShape(4.dp)
    ) {
        Text(
            text = "FERMÉ",
            color = MaterialTheme.colorScheme.onError,
            style = MaterialTheme.typography.labelSmall,
            modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
        )
    }
}

@Composable
private fun StationCounter(
    count: Int,
    icon: ImageVector,
    label: String? = null,
    isLarge: Boolean = false,
    isBold: Boolean = false
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
            icon,
            contentDescription = null,
            tint = iconColor,
            modifier = Modifier.size(24.dp)
        )
        Text(
            "$count",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = if (isBold) FontWeight.Black else FontWeight.Normal,
            color = textColor
        )
        if (isLarge && label != null) {
            Text(
                label,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontWeight = if (isBold) FontWeight.Bold else FontWeight.Normal
            )
        }
    }
}

// Helper
fun formatStationName(originalName: String): String {
    return originalName.replace(Regex("^\\d+\\s*-\\s*"), "")
}
