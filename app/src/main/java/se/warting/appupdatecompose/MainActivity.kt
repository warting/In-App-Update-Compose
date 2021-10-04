/*
 * MIT License
 *
 * Copyright (c) 2021 Stefan Wärting
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package se.warting.appupdatecompose

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import com.google.android.play.core.ktx.AppUpdateResult
import com.google.android.play.core.ktx.clientVersionStalenessDays
import kotlinx.coroutines.launch
import se.warting.inappupdate.compose.rememberInAppUpdateState

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                // A surface container using the 'background' color from the theme
                Surface(color = MaterialTheme.colors.background) {
                    InAppUpdate()
                }
            }
        }
    }
}

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
    Text(text = "NotAvailable")
}

@Composable
fun Available(appUpdateResult: AppUpdateResult.Available) {

    Text(
        text = "Available " +
                appUpdateResult.updateInfo.availableVersionCode() +
                " is availible since " +
                appUpdateResult.updateInfo.clientVersionStalenessDays +
                " days"
    )
}

@Composable
fun InProgress(appUpdateResult: AppUpdateResult.InProgress) {
    Text(text = "InProgress, downloaded: " + appUpdateResult.installState.bytesDownloaded())
}

@Composable
fun Downloaded(appUpdateResult: AppUpdateResult.Downloaded) {
    val scope = rememberCoroutineScope()
    Button(onClick = {
        scope.launch {
            appUpdateResult.completeUpdate()
        }
    }) {
        Text(text = "Install")
    }
}
