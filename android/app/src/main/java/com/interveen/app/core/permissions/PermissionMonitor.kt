package com.interveen.app.core.permissions

import android.Manifest
import android.accessibilityservice.AccessibilityServiceInfo
import android.app.AppOpsManager
import android.content.ComponentName
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Process
import android.provider.Settings
import android.view.accessibility.AccessibilityManager
import androidx.core.content.ContextCompat
import com.interveen.app.core.accessibility.InterveenAccessibilityService
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PermissionMonitor @Inject constructor(
    @ApplicationContext private val context: Context,
) {
    fun canDrawOverlays(): Boolean = Settings.canDrawOverlays(context)

    fun hasNotifications(): Boolean {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) return true
        return ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.POST_NOTIFICATIONS,
        ) == PackageManager.PERMISSION_GRANTED
    }

    fun needsRestrictedSettings(): Boolean =
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU

    fun areRestrictedSettingsAllowed(): Boolean {
        if (!needsRestrictedSettings()) return true
        if (isAccessibilityEnabled()) return true
        return try {
            val appOps = context.getSystemService(AppOpsManager::class.java) ?: return true
            val mode = appOps.unsafeCheckOpNoThrow(
                APP_OP_ACCESS_RESTRICTED_SETTINGS,
                Process.myUid(),
                context.packageName,
            )
            mode == AppOpsManager.MODE_ALLOWED
        } catch (_: Exception) {
            false
        }
    }

    fun isAccessibilityEnabled(): Boolean {
        val manager = context.getSystemService(Context.ACCESSIBILITY_SERVICE) as? AccessibilityManager
            ?: return false
        if (!manager.isEnabled) return false

        val expected = ComponentName(context, InterveenAccessibilityService::class.java)
        val enabledServices = Settings.Secure.getString(
            context.contentResolver,
            Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES,
        ).orEmpty()

        val listed = enabledServices
            .split(':')
            .mapNotNull { ComponentName.unflattenFromString(it) }
            .any { it == expected }

        val connected = manager.getEnabledAccessibilityServiceList(
            AccessibilityServiceInfo.FEEDBACK_GENERIC,
        ).any { info ->
            info.resolveInfo?.serviceInfo?.let { service ->
                service.packageName == expected.packageName && service.name == expected.className
            } == true
        }

        return listed || connected
    }

    companion object {
        private const val APP_OP_ACCESS_RESTRICTED_SETTINGS =
            "android:access_restricted_settings"
    }
}
