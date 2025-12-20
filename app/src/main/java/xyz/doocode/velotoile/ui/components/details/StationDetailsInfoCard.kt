package xyz.doocode.velotoile.ui.components.details

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
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
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun StationDetailsInfoCard(station: Station) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
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
                    imageVector = Icons.Filled.Info,
                    contentDescription = "Infos",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(end = 8.dp)
                )
                Text(
                    text = "Informations supplémentaires",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            // Number
            InfoRow(
                label = "Numéro de station",
                value = station.number.toString(),
                backgroundColor = Color(0xFFE0E0E0).copy(alpha = 0.5f)
            )

            /*// Contract
            InfoRow(
                label = "Contrat",
                value = station.contractName,
                backgroundColor = Color(0xFF64B5F6).copy(alpha = 0.1f),
                modifier = Modifier.padding(top = 8.dp)
            )*/

            // Banking
            InfoRow(
                label = "Paiement",
                value = if (station.banking) "Disponible" else "Non disponible",
                backgroundColor = if (station.banking) Color(0xFF4CAF50).copy(alpha = 0.1f) 
                    else Color(0xFFF44336).copy(alpha = 0.1f),
                modifier = Modifier.padding(top = 8.dp)
            )

            // Bonus
            InfoRow(
                label = "Bonus",
                value = if (station.bonus) "Oui" else "Non",
                backgroundColor = if (station.bonus) Color(0xFFFFD700).copy(alpha = 0.1f)
                    else Color(0xFFE0E0E0).copy(alpha = 0.5f),
                modifier = Modifier.padding(top = 8.dp)
            )

            // Last update
            val lastUpdateText = formatDate(station.lastUpdate)
            InfoRow(
                label = "Dernière maj",
                value = lastUpdateText,
                backgroundColor = Color(0xFFC8E6C9).copy(alpha = 0.3f),
                modifier = Modifier.padding(top = 8.dp)
            )
        }
    }
}

@Composable
private fun InfoRow(
    label: String,
    value: String,
    backgroundColor: Color,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(backgroundColor, RoundedCornerShape(8.dp))
            .padding(horizontal = 12.dp, vertical = 6.dp)
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
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

private fun formatDate(timestamp: Long): String {
    return try {
        val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.FRANCE)
        sdf.format(Date(timestamp))
    } catch (e: Exception) {
        "Non disponible"
    }
}
