package xyz.doocode.velotoile.ui.components

import StationsViewModel
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp

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
                label = "Ascendant",
                icon = Icons.Filled.ArrowUpward,
                isSelected = currentSortOrder.value == SortOrder.ASCENDING,
                onClick = { viewModel.setSortOrder(SortOrder.ASCENDING) }
            )

            SortOrderMenuItem(
                label = "Descendant",
                icon = Icons.Filled.ArrowDownward,
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
                Pair("Nom", SortField.NAME),
                Pair("Numéro", SortField.NUMBER),
                Pair("Nombre de vélos", SortField.TOTAL_BIKES),
                Pair("Vélos mécaniques", SortField.MECHANICAL_BIKES),
                Pair("Vélos électriques", SortField.ELECTRICAL_BIKES),
                Pair("Nombre de places", SortField.AVAILABLE_STANDS)
            )

            sortOptions.forEach { (label, field) ->
                SortFieldMenuItem(
                    label = label,
                    isSelected = currentSortField.value == field,
                    onClick = { viewModel.setSortField(field) }
                )
            }
        }
    }
}

@Composable
private fun SortOrderMenuItem(
    label: String,
    icon: ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    DropdownMenuItem(
        text = {
            Row(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = label,
                    modifier = Modifier.weight(1f)
                )
            }
        },
        leadingIcon = {
            Icon(icon, contentDescription = label)
        },
        trailingIcon = {
            if (isSelected) {
                Icon(Icons.Filled.CheckCircle, contentDescription = "Sélectionné")
            }
        },
        onClick = onClick
    )
}

@Composable
private fun SortFieldMenuItem(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    DropdownMenuItem(
        text = {
            Row(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = label,
                    modifier = Modifier.weight(1f)
                )
            }
        },
        trailingIcon = {
            if (isSelected) {
                Icon(Icons.Filled.CheckCircle, contentDescription = "Sélectionné")
            }
        },
        onClick = onClick
    )
}