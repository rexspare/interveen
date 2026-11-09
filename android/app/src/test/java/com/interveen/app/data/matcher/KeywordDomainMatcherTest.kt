package com.interveen.app.data.matcher

import com.interveen.app.domain.model.MatchReason
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class KeywordDomainMatcherTest {

    private val matcher = KeywordDomainMatcher()

    @Test
    fun matchesBareAdultDomain() {
        val result = matcher.evaluate("https://www.pornhub.com/video/watch")
        assertTrue(result.matched)
        assertEquals(MatchReason.DOMAIN, result.reason)
        assertEquals("pornhub.com", result.evidence)
    }

    @Test
    fun matchesKeywordInAddressBar() {
        val result = matcher.evaluate("https://www.google.com/search?q=free+porn")
        assertTrue(result.matched)
        assertEquals(MatchReason.KEYWORD, result.reason)
    }

    @Test
    fun ignoresOrdinaryNews() {
        val result = matcher.evaluate("https://www.theguardian.com/world")
        assertFalse(result.matched)
    }

    @Test
    fun matchesAdultTld() {
        val result = matcher.evaluate("https://wow.xxx/")
        assertTrue(result.matched)
        assertEquals(MatchReason.DOMAIN, result.reason)
    }

    @Test
    fun matchesAdultTokenInUnknownHost() {
        val result = matcher.evaluate("https://random-porn-tube.net/watch")
        assertTrue(result.matched)
        assertEquals(MatchReason.DOMAIN, result.reason)
    }

    @Test
    fun matchesUnknownXxxHost() {
        val result = matcher.evaluate("https://night-xxx-clips.tv/v/1")
        assertTrue(result.matched)
        assertEquals(MatchReason.DOMAIN, result.reason)
    }

    @Test
    fun ignoresXHome() {
        val result = matcher.evaluate("https://x.com/home")
        assertFalse(result.matched)
    }

    @Test
    fun ignoresFacebookHome() {
        val result = matcher.evaluate(
            """
            Facebook
            https://www.facebook.com/
            Home
            Friends
            Watch
            """.trimIndent(),
        )
        assertFalse(result.matched)
    }

    @Test
    fun matchesPornSearchOnX() {
        val result = matcher.evaluate("https://x.com/search?q=free+porn")
        assertTrue(result.matched)
        assertEquals(MatchReason.KEYWORD, result.reason)
    }

    @Test
    fun matchesNsfwPostOnX() {
        val result = matcher.evaluate(
            """
            x.com
            Home
            Following
            NSFW
            new clip tonight
            """.trimIndent(),
        )
        assertTrue(result.matched)
        assertEquals(MatchReason.KEYWORD, result.reason)
        assertEquals("nsfw", result.evidence)
    }

    @Test
    fun matchesAdultCaptionOnFacebook() {
        val result = matcher.evaluate(
            """
            Facebook
            Reels
            onlyfans drop in my bio
            Like
            Comment
            """.trimIndent(),
        )
        assertTrue(result.matched)
        assertEquals(MatchReason.KEYWORD, result.reason)
    }

    @Test
    fun matchesNsfwPathOnMixedHost() {
        val result = matcher.evaluate("https://www.reddit.com/r/nsfw/comments/abc")
        assertTrue(result.matched)
        assertEquals(MatchReason.KEYWORD, result.reason)
    }

    @Test
    fun ignoresJavaCom() {
        assertFalse(matcher.evaluate("https://www.java.com/").matched)
    }

    @Test
    fun ignoresPopcorn() {
        assertFalse(matcher.evaluate("https://recipes.example/popcorn").matched)
    }

    @Test
    fun honorsExtraDomain() {
        val result = matcher.evaluate(
            rawInput = "https://blocked.example/path",
            extraDomains = setOf("blocked.example"),
        )
        assertTrue(result.matched)
        assertEquals(MatchReason.DOMAIN, result.reason)
    }

    @Test
    fun extractsHostsFromNoisyAccessibilityDump() {
        val hosts = matcher.extractHosts(
            """
            Search or type URL
            www.xvideos.com
            New tab
            """.trimIndent(),
        )
        assertTrue(hosts.contains("xvideos.com"))
    }

    @Test
    fun mixedHostIsDetected() {
        assertTrue(matcher.isMixedHost("m.facebook.com"))
        assertTrue(matcher.isMixedHost("x.com"))
        assertFalse(matcher.isMixedHost("pornhub.com"))
    }

    @Test
    fun matchesChineseSearchOnBaidu() {
        val result = matcher.evaluate("https://www.baidu.com/s?wd=色情视频")
        assertTrue(result.matched)
        assertEquals(MatchReason.KEYWORD, result.reason)
    }

    @Test
    fun matchesPercentEncodedChineseSearch() {
        val result = matcher.evaluate("https://www.google.com/search?q=%E8%89%B2%E6%83%85")
        assertTrue(result.matched)
        assertEquals(MatchReason.KEYWORD, result.reason)
    }

    @Test
    fun matchesArabicCaptionOnFacebook() {
        val result = matcher.evaluate(
            """
            Facebook
            Reels
            فيديو سكس جديد
            Like
            Comment
            """.trimIndent(),
        )
        assertTrue(result.matched)
        assertEquals(MatchReason.KEYWORD, result.reason)
    }

    @Test
    fun matchesHindiSearchOnGoogle() {
        val result = matcher.evaluate("https://www.google.com/search?q=सेक्स+वीडियो")
        assertTrue(result.matched)
        assertEquals(MatchReason.KEYWORD, result.reason)
    }

    @Test
    fun stillMatchesEighteenPlus() {
        val result = matcher.evaluate("new 18+ clip tonight")
        assertTrue(result.matched)
        assertEquals("18+", result.evidence)
    }

    @Test
    fun matchesJapaneseKeyword() {
        val result = matcher.evaluate("https://search.yahoo.co.jp/search?p=エロ動画")
        assertTrue(result.matched)
        assertEquals(MatchReason.KEYWORD, result.reason)
    }

    @Test
    fun matchesKoreanKeyword() {
        val result = matcher.evaluate("https://search.naver.com/search.naver?query=야동")
        assertTrue(result.matched)
        assertEquals(MatchReason.KEYWORD, result.reason)
    }

    @Test
    fun matchesRussianKeywordOnVk() {
        val result = matcher.evaluate("https://vk.com/feed порно видео")
        assertTrue(result.matched)
        assertEquals(MatchReason.KEYWORD, result.reason)
    }

    @Test
    fun matchesIndonesianBokep() {
        val result = matcher.evaluate("https://www.google.com/search?q=bokep+indo")
        assertTrue(result.matched)
        assertEquals(MatchReason.KEYWORD, result.reason)
    }

    @Test
    fun matchesSpanishPornoSearch() {
        val result = matcher.evaluate("https://www.google.com/search?q=videos+porno")
        assertTrue(result.matched)
        assertEquals(MatchReason.KEYWORD, result.reason)
    }

    @Test
    fun matchesThaiKeyword() {
        val result = matcher.evaluate("คลิปโป๊ ใหม่")
        assertTrue(result.matched)
        assertEquals(MatchReason.KEYWORD, result.reason)
    }

    @Test
    fun matchesStandaloneChineseTerm() {
        val result = matcher.evaluate("黄片")
        assertTrue(result.matched)
        assertEquals(MatchReason.KEYWORD, result.reason)
    }

    @Test
    fun ignoresOrdinaryChineseNews() {
        val result = matcher.evaluate("https://news.example.cn/world 国际新闻 今日头条")
        assertFalse(result.matched)
    }

    @Test
    fun ignoresOrdinaryArabicNews() {
        val result = matcher.evaluate("https://www.aljazeera.net/news السياسة والاقتصاد")
        assertFalse(result.matched)
    }
}
