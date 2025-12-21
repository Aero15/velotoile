package xyz.doocode.velotoile.ui.components.search.menu

import android.content.res.Configuration
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import xyz.doocode.velotoile.ui.theme.VelotoileTheme

@Composable
fun SortFieldMenuItem(
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

@Preview(
    name = "Light mode",
    showBackground = true
)
@Preview(
    name = "Dark mode",
    uiMode = Configuration.UI_MODE_NIGHT_YES
)
@Composable
private fun SortFieldMenuItemPreview() {
    VelotoileTheme {
        SortFieldMenuItem(
            label = "Nom",
            isSelected = true,
            onClick = {}
        )
    }
}