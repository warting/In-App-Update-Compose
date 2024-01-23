package se.warting.appupdatecompose

import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import se.warting.inappupdate.compose.ForceRequireLatestVersion

@Composable
fun ForcedUpdate() {
    MaterialTheme {
        // A surface container using the 'background' color from the theme
        Surface(color = MaterialTheme.colors.background) {
            ForceRequireLatestVersion {
                AppContent{
                    Text("Forced update")
                }
            }
        }
    }
}