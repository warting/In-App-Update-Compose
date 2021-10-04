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

import android.content.Context
import android.graphics.Color
import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import com.google.android.material.snackbar.Snackbar
import com.google.android.play.core.appupdate.AppUpdateInfo
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.ktx.AppUpdateResult
import com.google.android.play.core.ktx.BuildConfig
import com.google.android.play.core.ktx.clientVersionStalenessDays
import com.google.android.play.core.ktx.isFlexibleUpdateAllowed
import com.google.android.play.core.ktx.isImmediateUpdateAllowed
import com.google.android.play.core.ktx.requestUpdateFlow
import com.google.android.play.core.ktx.updatePriority
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@Composable
internal fun rememberMutableInAppUpdateState(): InAppUpdateState {

    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    val inAppUpdateState = remember {
        MutableInAppUpdateState(scope, context, AppUpdateManagerFactory.create(context))
    }

    return inAppUpdateState
}

private const val MY_REQUEST_CODE = 5293487

private const val PRIO_1 = 1
private const val PRIO_2 = 2
private const val PRIO_3 = 3
private const val PRIO_4 = 4
private const val PRIO_5 = 5
private const val PRIO_4_IMMEDIATE_AFTER = 5
private const val PRIO_3_IMMEDIATE_AFTER = 30
private const val PRIO_3_FLEXIBLE_AFTER = 15
private const val PRIO_2_IMMEDIATE_AFTER = 90
private const val PRIO_2_FLEXIBLE_AFTER = 30
private const val PRIO_1_IMMEDIATE_AFTER = 365
private const val PRIO_1_FLEXIBLE_AFTER = 90

internal class MutableInAppUpdateState(
    private val scope: CoroutineScope,
    private val context: Context,
    private val appUpdateManager: AppUpdateManager,
) : InAppUpdateState {

    private var _appUpdateResult by mutableStateOf<AppUpdateResult>(AppUpdateResult.NotAvailable)

    override val appUpdateResult: AppUpdateResult
        get() = _appUpdateResult

    private var currentType = AppUpdateType.FLEXIBLE

    private fun startImmediateUpdateIfAllowed(info: AppUpdateInfo, n: AppUpdateResult.Available) {
        if (info.isImmediateUpdateAllowed) {
            n.startImmediateUpdate(context.findActivity(), MY_REQUEST_CODE)
            currentType = AppUpdateType.IMMEDIATE
        } else {
            startFlexibleUpdateIfAllowed(info, n)
        }
    }

    private fun startFlexibleUpdateIfAllowed(info: AppUpdateInfo, n: AppUpdateResult.Available) {
        if (info.isFlexibleUpdateAllowed) {
            n.startFlexibleUpdate(context.findActivity(), MY_REQUEST_CODE)
            currentType = AppUpdateType.FLEXIBLE
        }
    }

    init {
        scope.launch {
            appUpdateManager.requestUpdateFlow().catch { e ->
                Log.e("error", "", e)
            }.collect {
                _appUpdateResult = it
                handleUpdateResult(it)
            }
        }
    }

    private fun handleUpdateResult(it: AppUpdateResult): Unit =
        when (it) {
            is AppUpdateResult.NotAvailable -> notAvailable()
            is AppUpdateResult.Available -> available(it)
            is AppUpdateResult.InProgress -> inProgress(it)
            is AppUpdateResult.Downloaded -> isDownloaded(it)
        }

    private fun notAvailable() {
        Log.d("inappuppdate", "Available")
    }

    private fun available(appUpdateResult: AppUpdateResult.Available) {
        Log.d("inappuppdate", "available")

        val info = appUpdateResult.updateInfo
        val daysOld: Int = info.clientVersionStalenessDays ?: 0

        if (info.updatePriority == PRIO_5) { // Priority: 5 (Immediate update flow)
            startImmediateUpdateIfAllowed(info, appUpdateResult)
        } else if (info.updatePriority == PRIO_4) {
            if (daysOld > PRIO_4_IMMEDIATE_AFTER) {
                startImmediateUpdateIfAllowed(info, appUpdateResult)
            } else {
                startFlexibleUpdateIfAllowed(info, appUpdateResult)
            }
        } else if (info.updatePriority == PRIO_3) { // Priority: 3
            if (daysOld > PRIO_3_IMMEDIATE_AFTER) {
                startImmediateUpdateIfAllowed(info, appUpdateResult)
            } else if (daysOld > PRIO_3_FLEXIBLE_AFTER) {
                startFlexibleUpdateIfAllowed(info, appUpdateResult)
            }
        } else if (info.updatePriority == PRIO_2) { // Priority: 2
            if (daysOld > PRIO_2_IMMEDIATE_AFTER) {
                startImmediateUpdateIfAllowed(info, appUpdateResult)
            } else if (daysOld > PRIO_2_FLEXIBLE_AFTER) {
                startFlexibleUpdateIfAllowed(info, appUpdateResult)
            }
        } else if (info.updatePriority == PRIO_1) { // Priority: 1
            if (daysOld > PRIO_1_IMMEDIATE_AFTER) {
                startImmediateUpdateIfAllowed(info, appUpdateResult)
            } else if (daysOld > PRIO_1_FLEXIBLE_AFTER) {
                startFlexibleUpdateIfAllowed(info, appUpdateResult)
            }
        }
    }

    private fun inProgress(appUpdateResult: AppUpdateResult.InProgress) {
        if (BuildConfig.DEBUG) {
            Log.d(
                "inappuppdate",
                "inProgress. bytes downloaded: " + appUpdateResult.installState.bytesDownloaded()
            )
        }
    }

    private fun isDownloaded(appUpdateResult: AppUpdateResult.Downloaded) {
        if (currentType == AppUpdateType.FLEXIBLE) {
            flexibleUpdateDownloadCompleted()
        } else if (currentType == AppUpdateType.IMMEDIATE) {
            scope.launch {
                appUpdateResult.completeUpdate()
            }
        }
    }

    private fun flexibleUpdateDownloadCompleted() {
        Snackbar.make(
            context.findActivity().findViewById(android.R.id.content),
            "An update has just been downloaded.",
            Snackbar.LENGTH_INDEFINITE
        ).apply {
            setAction("RESTART") { appUpdateManager.completeUpdate() }
            setActionTextColor(Color.WHITE)
            show()
        }
    }
}
