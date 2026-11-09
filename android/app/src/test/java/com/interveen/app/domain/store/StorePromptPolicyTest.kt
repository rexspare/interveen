package com.interveen.app.domain.store

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class StorePromptPolicyTest {
    private val now = 1_700_000_000_000L

    @Test
    fun reviewWaitsThreeDaysThenRepeats() {
        val firstAsk = StorePromptPolicy.initialReviewAt(now)
        assertEquals(now + StorePromptPolicy.REVIEW_INTERVAL_MILLIS, firstAsk)
        assertFalse(StorePromptPolicy.reviewDue(false, firstAsk, now))
        assertTrue(StorePromptPolicy.reviewDue(false, firstAsk, firstAsk))
        assertEquals(now + StorePromptPolicy.REVIEW_INTERVAL_MILLIS, StorePromptPolicy.afterReviewShown(now))
    }

    @Test
    fun ratedUsersAreNeverAskedAgain() {
        assertFalse(StorePromptPolicy.reviewDue(true, now - 1, now))
    }

    @Test
    fun remindLaterWaitsFiveDays() {
        val next = StorePromptPolicy.afterRemindLater(now)
        assertEquals(now + StorePromptPolicy.REMIND_LATER_MILLIS, next)
        assertFalse(StorePromptPolicy.updateDue(true, next, now + StorePromptPolicy.REVIEW_INTERVAL_MILLIS))
        assertTrue(StorePromptPolicy.updateDue(true, next, next))
    }

    @Test
    fun updatePromptStaysHiddenWhenPlayHasNothing() {
        assertFalse(StorePromptPolicy.updateDue(false, null, now))
    }
}
