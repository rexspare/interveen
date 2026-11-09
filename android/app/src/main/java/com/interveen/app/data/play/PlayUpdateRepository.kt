package com.interveen.app.data.play

import android.content.Context
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import com.google.android.play.core.appupdate.AppUpdateInfo
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.appupdate.AppUpdateOptions
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.InstallStatus
import com.google.android.play.core.install.model.UpdateAvailability
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.resume
import kotlinx.coroutines.suspendCancellableCoroutine

enum class UpdateOffer {
    None,
    Available,
    ReadyToRestart,
}

@Singleton
class PlayUpdateRepository @Inject constructor(
    @ApplicationContext context: Context,
) {
    private val manager = AppUpdateManagerFactory.create(context)
    private var pending: AppUpdateInfo? = null

    suspend fun check(): UpdateOffer = suspendCancellableCoroutine { continuation ->
        manager.appUpdateInfo
            .addOnSuccessListener { info ->
                pending = info
                val offer = when {
                    info.installStatus() == InstallStatus.DOWNLOADED -> UpdateOffer.ReadyToRestart
                    info.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE &&
                        (info.isUpdateTypeAllowed(AppUpdateType.FLEXIBLE) ||
                            info.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE)) -> UpdateOffer.Available
                    info.updateAvailability() == UpdateAvailability.DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS ->
                        UpdateOffer.Available
                    else -> UpdateOffer.None
                }
                if (continuation.isActive) continuation.resume(offer)
            }
            .addOnFailureListener {
                pending = null
                if (continuation.isActive) continuation.resume(UpdateOffer.None)
            }
    }

    fun start(launcher: ActivityResultLauncher<IntentSenderRequest>): Boolean {
        val info = pending ?: return false
        val type = when {
            info.isUpdateTypeAllowed(AppUpdateType.FLEXIBLE) -> AppUpdateType.FLEXIBLE
            info.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE) -> AppUpdateType.IMMEDIATE
            else -> return false
        }
        return try {
            manager.startUpdateFlowForResult(
                info,
                launcher,
                AppUpdateOptions.newBuilder(type).build(),
            )
        } catch (_: RuntimeException) {
            false
        }
    }

    fun complete() {
        manager.completeUpdate()
    }
}
