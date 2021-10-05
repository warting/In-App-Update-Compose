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

package se.warting.inappupdate.compose

import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import com.google.android.play.core.ktx.AppUpdateResult
import kotlinx.coroutines.launch

// Try launch update in another way
private const val APP_UPDATE_REQUEST_CODE = 86500

@Composable
fun RequireLatestVersion(content: @Composable () -> Unit) {
    val inAppUpdateState = rememberInAppUpdateState()
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    when (val state = inAppUpdateState.appUpdateResult) {
        AppUpdateResult.NotAvailable -> content()
        is AppUpdateResult.Available -> {
            UpdateRequiredView {
                state.startImmediateUpdate(context.findActivity(), APP_UPDATE_REQUEST_CODE)
            }
        }
        is AppUpdateResult.InProgress -> {
            UpdateInProgress(
                progress = state.installState.bytesDownloaded()
                    .toFloat() / state.installState.totalBytesToDownload().toFloat()
            )
        }
        is AppUpdateResult.Downloaded -> {
            UpdateDownloadedView {
                scope.launch {
                    state.completeUpdate()
                }
            }
        }
    }
}
