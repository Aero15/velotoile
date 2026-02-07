package xyz.doocode.velotoile.ui.components.search.menu

import SortField
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.DirectionsBike
import androidx.compose.material.icons.filled.ElectricBike
import androidx.compose.material.icons.filled.LocalParking
import androidx.compose.material.icons.filled.NearMe
import androidx.compose.material.icons.filled.NorthEast
import androidx.compose.material.icons.filled.PedalBike
import androidx.compose.material.icons.filled.SouthEast
import androidx.compose.material.icons.filled.Tag
import androidx.compose.material.icons.filled.TextFields
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.*
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.*
import xyz.doocode.velotoile.ui.viewmodel.StationsViewModel

@Composable
fun SortMenu(
    isMenuOpen: Boolean,
    onMenuOpenChange: (Boolean) -> Unit,
    viewModel: StationsViewModel,
    modifier: Modifier = Modifier
) {
    val currentSortField = viewModel.sortField.observeAsState()
    val currentSortOrder = viewModel.sortOrder.observeAsState()

    Box(modifier = modifier, contentAlignment = Alignment.TopEnd) {
        DropdownMenu(
            expanded = isMenuOpen,
            onDismissRequest = { onMenuOpenChange(false) },
            offset = DpOffset(x = 0.dp, y = 8.dp)
        ) {
            // Sort Order Section
            Text(
                text = "Ordre de tri",
                style = MaterialTheme.typography.labelSmall,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            SortOrderMenuItem(
                label = "Croissant",
                icon = Icons.Filled.NorthEast,
                isSelected = currentSortOrder.value == SortOrder.ASCENDING,
                onClick = { viewModel.setSortOrder(SortOrder.ASCENDING) }
            )

            SortOrderMenuItem(
                label = "Décroissant",
                icon = Icons.Filled.SouthEast,
                isSelected = currentSortOrder.value == SortOrder.DESCENDING,
                onClick = { viewModel.setSortOrder(SortOrder.DESCENDING) }
            )

            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

            // Sort Field Section
            Text(
                text = "Trier par",
                style = MaterialTheme.typography.labelSmall,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            val sortOptions = listOf(
                SortOption("Nom", SortField.NAME, Icons.Default.TextFields),
                SortOption("Numéro", SortField.NUMBER, Icons.Default.Tag),
                SortOption(
                    "Vélos disponibles", SortField.TOTAL_BIKES,
                    Icons.AutoMirrored.Filled.DirectionsBike
                ),
                SortOption("Vélos mécaniques", SortField.MECHANICAL_BIKES, Icons.Filled.PedalBike),
                SortOption(
                    "Vélos électriques",
                    SortField.ELECTRICAL_BIKES,
                    Icons.Filled.ElectricBike
                ),
                SortOption(
                    "Nombre de places",
                    SortField.AVAILABLE_STANDS,
                    Icons.Filled.LocalParking
                ),
                SortOption("Proximité", SortField.PROXIMITY, Icons.Default.NearMe)
            )

            sortOptions.forEach { option ->
                SortOrderMenuItem(
                    label = option.label,
                    icon = option.icon,
                    isSelected = currentSortField.value == option.field,
                    onClick = { viewModel.setSortField(option.field) }
                )
            }

        }
    }
}

data class SortOption(
    val label: String,
    val field: SortField,
    val icon: ImageVector
)

