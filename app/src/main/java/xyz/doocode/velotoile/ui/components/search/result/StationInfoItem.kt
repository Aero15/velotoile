package xyz.doocode.velotoile.ui.components.search.result

import android.content.res.Configuration
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.DirectionsBike
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import xyz.doocode.velotoile.ui.theme.VelotoileTheme

@Composable
fun StationInfoItem(
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
                .size(28.dp)
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
private fun StationInfoItemPreview() {
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
    return when (value) {
        0 -> Color(0xFFD32F2F)
        in 1..2 -> Color(0xFFFF9800)
        else -> MaterialTheme.colorScheme.primary
    }
}

@Composable
private fun getTextColor(value: Int): Color {
    return when (value) {
        0 -> Color(0xFFD32F2F)
        in 1..2 -> Color(0xFFFF9800)
        else -> MaterialTheme.colorScheme.onSurface
    }
}