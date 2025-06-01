package se.warting.inappupdate.compose

import androidx.compose.runtime.Composable
import com.google.android.play.core.ktx.AppUpdateResult
import org.junit.Test

/**
 * Test that verifies the InAppUpdateState API supports both:
 * 1. Direct pattern matching on InAppUpdateState types (existing API)
 * 2. Pattern matching on appUpdateResult property (new API from README)
 */
class InAppUpdateStateApiTest {

    /**
     * Test the existing API pattern - direct matching on InAppUpdateState
     * This should continue to work as before
     */
    @Test
    fun testExistingApi() {
        // This is how the API was used before - in CustomUpdate.kt style
        // This pattern should continue to work
        @Composable
        fun ExistingApiExample() {
            val updateState = rememberInAppUpdateState()
            when (updateState) {
                is InAppUpdateState.DownloadedUpdate -> {
                    // Can access downloadResult property
                    val downloadResult = updateState.downloadResult
                    // Can also access the new appUpdateResult property
                    val appUpdateResult = updateState.appUpdateResult
                }
                is InAppUpdateState.InProgressUpdate -> {
                    // Can access installState
                    val installState = updateState.installState
                    // Can also access the new appUpdateResult property
                    val appUpdateResult = updateState.appUpdateResult
                }
                InAppUpdateState.Loading -> {
                    // Can access the new appUpdateResult property
                    val appUpdateResult = updateState.appUpdateResult
                }
                InAppUpdateState.NotAvailable -> {
                    // Can access the new appUpdateResult property  
                    val appUpdateResult = updateState.appUpdateResult
                }
                is InAppUpdateState.OptionalUpdate -> {
                    // Can access existing properties
                    val appUpdateInfo = updateState.appUpdateInfo
                    // Can also access the new appUpdateResult property
                    val appUpdateResult = updateState.appUpdateResult
                }
                is InAppUpdateState.RequiredUpdate -> {
                    // Can access existing properties
                    val appUpdateInfo = updateState.appUpdateInfo
                    // Can also access the new appUpdateResult property
                    val appUpdateResult = updateState.appUpdateResult
                }
                is InAppUpdateState.Error -> {
                    // Can access exception
                    val exception = updateState.exception
                    // Can also access the new appUpdateResult property
                    val appUpdateResult = updateState.appUpdateResult
                }
            }
        }
    }

    /**
     * Test the new API pattern from README - matching on appUpdateResult
     * This is the API that was documented but didn't work before
     */
    @Test
    fun testNewApiFromReadme() {
        // This is the API pattern shown in the README that should now work
        @Composable
        fun InAppUpdate() {
            val updateState = rememberInAppUpdateState()
            when (val result = updateState.appUpdateResult) {
                is AppUpdateResult.NotAvailable -> NotAvailable()
                is AppUpdateResult.Available -> Available(result)
                is AppUpdateResult.InProgress -> InProgress(result)
                is AppUpdateResult.Downloaded -> Downloaded(result)
            }
        }

        @Composable
        fun NotAvailable() {
            // Handle not available case
        }

        @Composable
        fun Available(result: AppUpdateResult.Available) {
            // Handle available case - can access updateInfo
            val updateInfo = result.updateInfo
        }

        @Composable
        fun InProgress(result: AppUpdateResult.InProgress) {
            // Handle in progress case - can access installState
            val installState = result.installState
        }

        @Composable
        fun Downloaded(result: AppUpdateResult.Downloaded) {
            // Handle downloaded case - can call completeUpdate()
            // result.completeUpdate() would be called in a coroutine
        }
    }
}