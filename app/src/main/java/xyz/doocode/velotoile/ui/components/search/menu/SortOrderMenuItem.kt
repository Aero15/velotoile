package xyz.doocode.velotoile.ui.components.search.menu

import android.content.res.Configuration
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.SortByAlpha
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import xyz.doocode.velotoile.ui.theme.VelotoileTheme

@Composable
fun SortOrderMenuItem(
    label: String,
    icon: ImageVector,
    isSelected: Boolean = false,
    onClick: () -> Unit = {}
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

@Preview(
    name = "Light mode",
    showBackground = true
)
@Preview(
    name = "Dark mode",
    uiMode = Configuration.UI_MODE_NIGHT_YES
)
@Composable
private fun SortOrderMenuItemPreview() {
    VelotoileTheme {
        SortOrderMenuItem(
            label = "Ascendant",
            icon = Icons.Filled.SortByAlpha,
            isSelected = true
        )
    }
}