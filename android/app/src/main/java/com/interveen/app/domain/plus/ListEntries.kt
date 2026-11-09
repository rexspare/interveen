package com.interveen.app.domain.plus

object ListEntries {
    fun domain(raw: String): String? {
        val host = raw.trim()
            .lowercase()
            .removePrefix("https://")
            .removePrefix("http://")
            .substringBefore("/")
            .substringBefore("?")
            .substringBefore("#")
            .removePrefix("www.")
            .trim('.')
        if (host.length < 4 || '.' !in host) return null
        if (host.any { !(it.isLetterOrDigit() || it == '.' || it == '-') }) return null
        return host
    }

    fun keyword(raw: String): String? {
        val word = raw.trim().lowercase().replace(Regex("\\s+"), " ")
        if (word.length < 2) return null
        return word
    }
}
