package com.interveen.app.domain.store

object StorePromptPolicy {
    const val REVIEW_INTERVAL_MILLIS = 3L * 24 * 60 * 60 * 1000
    const val REMIND_LATER_MILLIS = 5L * 24 * 60 * 60 * 1000

    fun initialReviewAt(firstOpenAt: Long): Long = firstOpenAt + REVIEW_INTERVAL_MILLIS

    fun reviewDue(hasRated: Boolean, nextAtMillis: Long?, nowMillis: Long): Boolean {
        if (hasRated || nextAtMillis == null) return false
        return nowMillis >= nextAtMillis
    }

    fun updateDue(updateAvailable: Boolean, remindAtMillis: Long?, nowMillis: Long): Boolean {
        if (!updateAvailable) return false
        return remindAtMillis == null || nowMillis >= remindAtMillis
    }

    fun afterReviewShown(nowMillis: Long): Long = nowMillis + REVIEW_INTERVAL_MILLIS

    fun afterRemindLater(nowMillis: Long): Long = nowMillis + REMIND_LATER_MILLIS
}
