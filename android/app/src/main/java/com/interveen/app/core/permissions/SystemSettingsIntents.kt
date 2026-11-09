package com.interveen.app.core.permissions

import android.content.Context
import android.content.Intent
import android.provider.Settings

object SystemSettingsIntents {
    fun openAppsList(context: Context) {
        val candidates = listOf(
            Intent(Settings.ACTION_APPLICATION_SETTINGS),
            Intent(Settings.ACTION_MANAGE_APPLICATIONS_SETTINGS),
            Intent(Settings.ACTION_MANAGE_ALL_APPLICATIONS_SETTINGS),
            Intent(Settings.ACTION_SETTINGS),
        )
        for (intent in candidates) {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            if (intent.resolveActivity(context.packageManager) != null) {
                context.startActivity(intent)
                return
            }
        }
    }
}
