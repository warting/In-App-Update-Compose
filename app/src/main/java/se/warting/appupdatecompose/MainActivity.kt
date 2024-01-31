package se.warting.appupdatecompose

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material.Text
import androidx.compose.runtime.collectAsState
import kotlinx.coroutines.ExperimentalCoroutinesApi
import se.eelde.toggles.flow.TogglesImpl
import se.warting.inappupdate.compose.NoUi
import se.warting.inappupdate.compose.material.MaterialRequireLatestVersion

enum class UpdateMode {
    NONE, FORCED, CUSTOM, MATERIAL, OPINIATED_NO_UI
}

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalCoroutinesApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val toggles = TogglesImpl(applicationContext)

        setContent {

            val updateMode =
                toggles.toggle("custom_update", UpdateMode::class.java, UpdateMode.NONE)
                    .collectAsState(initial = UpdateMode.NONE)

            when (updateMode.value) {
                UpdateMode.FORCED -> ForcedUpdate()
                UpdateMode.CUSTOM -> CustomUpdate()
                UpdateMode.MATERIAL -> MaterialRequireLatestVersion {
                    AppContent {
                        Text("Material update")
                    }
                }

                UpdateMode.OPINIATED_NO_UI -> NoUi(errorContent = {
                    AppContent {
                        Text("Material update failed")
                    }
                }, content = {
                    AppContent {
                        Text("Material update")
                    }
                })

                UpdateMode.NONE -> TogglesPrompt()
            }
        }
    }
}



