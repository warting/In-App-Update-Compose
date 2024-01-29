package se.warting.inappupdate.compose.material

import androidx.compose.material.AlertDialog
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.tooling.preview.Preview

@Preview
@Composable
private fun UpdateDownloadedViewPreview() {
    OptionalUpdateDownloadedView(update = {})
}

@Composable
internal fun OptionalUpdateDownloadedView(update: () -> Unit) {

    var showDialog by remember { mutableStateOf(true) }

    if (showDialog) {
        AlertDialog(onDismissRequest = { showDialog = false },
            title = { Text(text = "Update downloaded") },
            text = {
                Text(
                    text = "Restart now?"
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    update()
                }) {
                    Text(text = "Restart")
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    showDialog = false
                }) {
                    Text(text = "Not now")
                }
            }
        )
    }
}
