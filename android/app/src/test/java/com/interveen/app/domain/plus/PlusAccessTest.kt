package com.interveen.app.domain.plus

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class PlusAccessTest {
    private val start = 1_700_000_000_000L

    @Test
    fun trialLastsSevenDays() {
        assertEquals(PlusPhase.Trial, PlusAccess.phase(false, start, start))
        assertEquals(PlusPhase.Trial, PlusAccess.phase(false, start, start + PlusAccess.TRIAL_MILLIS - 1))
        assertEquals(PlusPhase.Expired, PlusAccess.phase(false, start, start + PlusAccess.TRIAL_MILLIS))
    }

    @Test
    fun subscriptionCoversAnExpiredTrial() {
        assertTrue(PlusAccess.hasPlus(true, start, start + PlusAccess.TRIAL_MILLIS * 2))
        assertEquals(PlusPhase.Subscribed, PlusAccess.phase(true, null, start))
    }

    @Test
    fun trialStartsOnFirstOpen() {
        assertTrue(PlusAccess.shouldStartTrial(null))
        assertFalse(PlusAccess.shouldStartTrial(start))
    }
}

class PriceComparisonsTest {
    @Test
    fun eachVisitAdvancesWithinTheCountry() {
        val first = PriceComparisons.item("PK", 0)
        val second = PriceComparisons.item("PK", 1)
        assertEquals("a cup of chai", first)
        assertEquals("a samosa", second)
        assertNotEquals(first, second)
    }

    @Test
    fun countriesDoNotShareTheSameFirstLine() {
        assertNotEquals(PriceComparisons.item("US", 0), PriceComparisons.item("JP", 0))
        assertNotEquals(PriceComparisons.item("GB", 0), PriceComparisons.item("IN", 0))
    }

    @Test
    fun unknownCountryUsesTheFallbackAndWraps() {
        val first = PriceComparisons.item("", 0)
        assertEquals("a cup of tea", first)
        assertEquals(first, PriceComparisons.item("ZZ", 4))
    }
}
