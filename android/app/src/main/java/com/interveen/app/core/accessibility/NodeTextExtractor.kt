package com.interveen.app.core.accessibility

import android.os.Build
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo

object NodeTextExtractor {

    private const val MAX_NODES = 500
    private const val MAX_CHARS = 16_000

    fun extract(event: AccessibilityEvent, root: AccessibilityNodeInfo?): String {
        val pieces = LinkedHashSet<String>()
        var total = 0

        fun add(value: String?) {
            val text = value?.trim().orEmpty()
            if (text.isEmpty() || text in pieces) return
            if (isTooShort(text)) return
            if (total + text.length > MAX_CHARS) return
            pieces += text
            total += text.length + 1
        }

        event.text?.forEach { add(it?.toString()) }
        add(event.contentDescription?.toString())
        if (root != null) {
            walk(root, ::add, 0)
        }
        return pieces.joinToString(separator = "\n")
    }

    private fun isTooShort(text: String): Boolean {
        if (text.length >= 3) return false
        if (text.length < 2) return true
        return text.all { it.code < 128 }
    }

    private fun walk(
        node: AccessibilityNodeInfo,
        add: (String?) -> Unit,
        visited: Int,
    ): Int {
        var count = visited + 1
        if (count > MAX_NODES) return count

        add(node.text?.toString())
        add(node.contentDescription?.toString())
        add(node.hintText?.toString())
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            add(node.tooltipText?.toString())
            add(node.paneTitle?.toString())
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            add(node.stateDescription?.toString())
        }

        for (i in 0 until node.childCount) {
            val child = node.getChild(i) ?: continue
            count = walk(child, add, count)
            if (count > MAX_NODES) break
        }
        return count
    }
}
