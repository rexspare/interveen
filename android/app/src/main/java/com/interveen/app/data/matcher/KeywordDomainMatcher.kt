package com.interveen.app.data.matcher

import com.interveen.app.data.local.DefaultBlocklist
import com.interveen.app.domain.model.MatchReason
import com.interveen.app.domain.model.MatchResult
import com.interveen.app.domain.repository.ContentMatcher
import java.text.Normalizer
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class KeywordDomainMatcher @Inject constructor() : ContentMatcher {

    private val urlRegex = Regex(
        """(?:https?://)?((?:[a-z0-9-]+\.)+[a-z]{2,})(/[^\s]*)?""",
        RegexOption.IGNORE_CASE,
    )

    private val defaultKeywordChecks: List<Pair<String, (String) -> Boolean>> by lazy {
        DefaultBlocklist.keywords.map { keyword ->
            val needle = normalizeText(keyword)
            needle to keywordCheck(needle)
        }
    }

    fun evaluate(rawInput: String): MatchResult {
        return evaluate(rawInput, extraKeywords = emptySet(), extraDomains = emptySet())
    }

    override fun evaluate(
        rawInput: String,
        extraKeywords: Set<String>,
        extraDomains: Set<String>,
    ): MatchResult {
        val haystack = normalizeText(rawInput)
        if (tooShort(haystack)) return MatchResult.None

        val hosts = extractHosts(haystack)
        val domains = DefaultBlocklist.domains + extraDomains.map { normalizeHost(it) }

        hosts.forEach { host ->
            if (isMixedHost(host)) return@forEach

            val match = domains.firstOrNull { domain -> host == domain || host.endsWith(".$domain") }
            if (match != null) {
                return MatchResult(
                    matched = true,
                    reason = MatchReason.DOMAIN,
                    evidence = match,
                )
            }
            val tld = host.substringAfterLast('.', missingDelimiterValue = "")
            if (tld in DefaultBlocklist.adultTlds) {
                return MatchResult(
                    matched = true,
                    reason = MatchReason.DOMAIN,
                    evidence = host,
                )
            }
            if (hostLooksAdult(host)) {
                return MatchResult(
                    matched = true,
                    reason = MatchReason.DOMAIN,
                    evidence = host,
                )
            }
        }

        DefaultBlocklist.pathTokens.forEach { token ->
            val needle = normalizeText(token)
            if (needle.isNotEmpty() && haystack.contains(needle)) {
                return MatchResult(
                    matched = true,
                    reason = MatchReason.KEYWORD,
                    evidence = token.trim('/'),
                )
            }
        }

        defaultKeywordChecks.forEach { (keyword, check) ->
            if (keyword.isNotEmpty() && check(haystack)) {
                return MatchResult(
                    matched = true,
                    reason = MatchReason.KEYWORD,
                    evidence = keyword.trim(),
                )
            }
        }

        extraKeywords
            .map { normalizeText(it) }
            .filter { it.length >= 2 }
            .forEach { keyword ->
                if (keywordCheck(keyword)(haystack)) {
                    return MatchResult(
                        matched = true,
                        reason = MatchReason.KEYWORD,
                        evidence = keyword.trim(),
                    )
                }
            }

        return MatchResult.None
    }

    fun extractHosts(rawInput: String): Set<String> {
        return urlRegex.findAll(normalizeText(rawInput))
            .map { normalizeHost(it.groupValues[1]) }
            .filter { it.contains('.') }
            .toSet()
    }

    private fun normalizeHost(value: String): String {
        return value
            .lowercase(Locale.ROOT)
            .removePrefix("www.")
            .trim()
            .trimEnd('.')
    }

    internal fun isMixedHost(host: String): Boolean {
        return DefaultBlocklist.mixedHosts.any { item -> host == item || host.endsWith(".$item") }
    }

    internal fun hostLooksAdult(host: String): Boolean {
        host.split('.').forEach { label ->
            if (label == "jav" || (label.startsWith("jav") && !label.startsWith("java"))) {
                return true
            }
            if (DefaultBlocklist.hostTokens.any { token -> label.contains(token) }) {
                return true
            }
        }
        return false
    }

    private fun keywordCheck(needle: String): (String) -> Boolean {
        val asciiWord = needle.all { it.code < 128 && it.isLetterOrDigit() }
        if (!asciiWord) {
            return { haystack -> haystack.contains(needle) }
        }
        val pattern = Regex("(?<![a-z0-9])${Regex.escape(needle)}(?![a-z0-9])")
        return { haystack -> pattern.containsMatchIn(haystack) }
    }

    internal fun normalizeText(raw: String): String {
        val decoded = decodePercentSequences(raw)
            .replace(plusBetweenTokens, " ")
        return Normalizer.normalize(decoded, Normalizer.Form.NFKC)
            .lowercase(Locale.ROOT)
            .trim()
    }

    private val plusBetweenTokens = Regex("(?<=\\p{L}|\\p{N})\\+(?=\\p{L}|\\p{N})")

    private fun tooShort(haystack: String): Boolean {
        if (haystack.length < 2) return true
        if (haystack.length >= 3) return false
        return haystack.all { it.code < 128 }
    }

    internal fun decodePercentSequences(value: String): String {
        if (!value.contains('%')) return value
        return buildString(value.length) {
            var index = 0
            while (index < value.length) {
                if (value[index] == '%' && index + 2 < value.length) {
                    val bytes = ArrayList<Byte>()
                    var cursor = index
                    while (
                        cursor + 2 < value.length &&
                        value[cursor] == '%' &&
                        value[cursor + 1].isHex() &&
                        value[cursor + 2].isHex()
                    ) {
                        val hex = value.substring(cursor + 1, cursor + 3)
                        bytes += hex.toInt(16).toByte()
                        cursor += 3
                    }
                    if (bytes.isNotEmpty()) {
                        append(String(bytes.toByteArray(), Charsets.UTF_8))
                        index = cursor
                        continue
                    }
                }
                append(value[index])
                index++
            }
        }
    }

    private fun Char.isHex(): Boolean = this in '0'..'9' || this in 'a'..'f' || this in 'A'..'F'
}
