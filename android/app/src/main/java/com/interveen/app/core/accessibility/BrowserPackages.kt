package com.interveen.app.core.accessibility

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build

object BrowserPackages {
    val ALL: Set<String> = setOf(
        "com.android.chrome",
        "com.chrome.beta",
        "com.chrome.dev",
        "com.chrome.canary",
        "com.google.android.googlequicksearchbox",
        "org.mozilla.firefox",
        "org.mozilla.firefox_beta",
        "org.mozilla.fenix",
        "org.mozilla.focus",
        "org.mozilla.firefox_nightly",
        "com.brave.browser",
        "com.brave.browser_beta",
        "com.microsoft.emmx",
        "com.microsoft.emmx.beta",
        "com.opera.browser",
        "com.opera.browser.beta",
        "com.opera.mini.native",
        "com.sec.android.app.sbrowser",
        "com.sec.android.app.sbrowser.beta",
        "com.duckduckgo.mobile.android",
        "com.vivaldi.browser",
        "com.kiwibrowser.browser",
        "com.ecosia.android",
        "org.chromium.chrome",
        "com.android.browser",
        "com.huawei.browser",
        "com.mi.globalbrowser",
        "com.coloros.browser",
        "com.heytap.browser",
        "com.vivo.browser",
        "com.uc.browser.en",
        "com.UCMobile.intl",
        "com.cloudmosa.puffin",
    )

    /**
     * Mixed platforms where adult content appears inside an otherwise ordinary feed.
     * These apps are watched; the whole network is never treated as an adult site.
     */
    val PLATFORMS: Set<String> = setOf(
        "com.twitter.android",
        "com.twitter.android.lite",
        "com.facebook.katana",
        "com.facebook.lite",
        "com.facebook.mlite",
        "com.instagram.android",
        "com.instagram.barcelona",
        "com.reddit.frontpage",
        "com.zhiliaoapp.musically",
        "com.ss.android.ugc.trill",
        "com.google.android.youtube",
        "com.snapchat.android",
        "com.discord",
        "org.telegram.messenger",
        "org.telegram.messenger.web",
        "org.thunderdog.challegram",
        "com.tumblr",
        "xyz.blueskyweb.app",
        "com.pinterest",
        "tv.twitch.android.app",
    )

    @Volatile
    private var discovered: Set<String> = emptySet()

    @Volatile
    private var lastDiscoverAt: Long = 0L

    fun isMonitored(context: Context, packageName: String): Boolean {
        if (packageName.isBlank() || packageName == context.packageName) return false
        if (packageName in ALL || packageName in PLATFORMS) return true
        refreshIfNeeded(context)
        if (packageName in discovered) return true
        refresh(context)
        return packageName in discovered
    }

    fun refresh(context: Context) {
        discovered = discover(context)
        lastDiscoverAt = System.currentTimeMillis()
    }

    private fun refreshIfNeeded(context: Context) {
        if (System.currentTimeMillis() - lastDiscoverAt > 15_000L) {
            refresh(context)
        }
    }

    private fun discover(context: Context): Set<String> {
        val pm = context.packageManager
        val found = linkedSetOf<String>()
        query(pm, Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_APP_BROWSER))
            .forEach { found += it }
        val https = Intent(Intent.ACTION_VIEW, Uri.parse("https://example.com"))
            .addCategory(Intent.CATEGORY_BROWSABLE)
        query(pm, https).forEach { found += it }
        resolveDefault(pm, https)?.let { found += it }
        found.remove(context.packageName)
        return found
    }

    private fun query(pm: PackageManager, intent: Intent): List<String> {
        val resolved = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            pm.queryIntentActivities(intent, PackageManager.ResolveInfoFlags.of(0))
        } else {
            @Suppress("DEPRECATION")
            pm.queryIntentActivities(intent, 0)
        }
        return resolved.mapNotNull { it.activityInfo?.packageName }
    }

    private fun resolveDefault(pm: PackageManager, intent: Intent): String? {
        val resolved = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            pm.resolveActivity(intent, PackageManager.ResolveInfoFlags.of(0))
        } else {
            @Suppress("DEPRECATION")
            pm.resolveActivity(intent, 0)
        }
        return resolved?.activityInfo?.packageName
    }
}
