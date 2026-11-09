package com.interveen.app.core.play

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri

object PlayStore {
    const val APP_ID = "com.interveen.app"
    const val LISTING = "https://play.google.com/store/apps/details?id=$APP_ID"

    fun openListing(context: Context) {
        val market = Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=$APP_ID")).apply {
            setPackage("com.android.vending")
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        val web = Intent(Intent.ACTION_VIEW, Uri.parse(LISTING)).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        try {
            context.startActivity(market)
        } catch (_: ActivityNotFoundException) {
            context.startActivity(web)
        }
    }
}
