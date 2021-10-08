package se.warting.appupdatecompose

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import se.warting.inappupdate.compose.RequireLatestVersion

class UiActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                // A surface container using the 'background' color from the theme
                Surface(color = MaterialTheme.colors.background) {
                    RequireLatestVersion {
                        Welcome()
                    }
                }
            }
        }
    }
}

@Composable
fun Welcome() {
    Text(text = "You have the latest version installed!")
}
