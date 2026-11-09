package com.interveen.app.core.overlay

import android.graphics.PixelFormat
import android.os.Build
import android.view.Gravity
import android.view.View
import android.view.WindowManager
import android.view.WindowManager.LayoutParams

class OverlayWindowController(
    private val windowManager: WindowManager,
) {
    private var attached: View? = null

    fun attach(view: View) {
        if (attached === view) return
        detach()
        windowManager.addView(view, layoutParams())
        attached = view
    }

    fun detach() {
        attached?.let { current ->
            runCatching { windowManager.removeViewImmediate(current) }
        }
        attached = null
    }

    private fun layoutParams(): LayoutParams {
        val type = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            LayoutParams.TYPE_APPLICATION_OVERLAY
        } else {
            @Suppress("DEPRECATION")
            LayoutParams.TYPE_SYSTEM_ALERT
        }
        return LayoutParams(
            LayoutParams.MATCH_PARENT,
            LayoutParams.MATCH_PARENT,
            type,
            LayoutParams.FLAG_LAYOUT_IN_SCREEN or
                LayoutParams.FLAG_KEEP_SCREEN_ON,
            PixelFormat.TRANSLUCENT,
        ).apply {
            gravity = Gravity.CENTER
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                layoutInDisplayCutoutMode = LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES
            }
        }
    }
}
