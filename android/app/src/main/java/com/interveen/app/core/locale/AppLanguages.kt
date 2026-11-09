package com.interveen.app.core.locale

data class AppLanguage(
    val tag: String,
    val nativeName: String,
)

object AppLanguages {
    val all: List<AppLanguage> = listOf(
        AppLanguage("en", "English"),
        AppLanguage("ar", "العربية"),
        AppLanguage("bn", "বাংলা"),
        AppLanguage("cs", "Čeština"),
        AppLanguage("da", "Dansk"),
        AppLanguage("de", "Deutsch"),
        AppLanguage("el", "Ελληνικά"),
        AppLanguage("es", "Español"),
        AppLanguage("fa", "فارسی"),
        AppLanguage("fi", "Suomi"),
        AppLanguage("fil", "Filipino"),
        AppLanguage("fr", "Français"),
        AppLanguage("gu", "ગુજરાતી"),
        AppLanguage("he", "עברית"),
        AppLanguage("hi", "हिन्दी"),
        AppLanguage("hu", "Magyar"),
        AppLanguage("id", "Bahasa Indonesia"),
        AppLanguage("it", "Italiano"),
        AppLanguage("ja", "日本語"),
        AppLanguage("ko", "한국어"),
        AppLanguage("mr", "मराठी"),
        AppLanguage("ms", "Bahasa Melayu"),
        AppLanguage("nb", "Norsk"),
        AppLanguage("nl", "Nederlands"),
        AppLanguage("pa", "ਪੰਜਾਬੀ"),
        AppLanguage("pl", "Polski"),
        AppLanguage("pt", "Português"),
        AppLanguage("pt-BR", "Português (Brasil)"),
        AppLanguage("ro", "Română"),
        AppLanguage("ru", "Русский"),
        AppLanguage("sv", "Svenska"),
        AppLanguage("sw", "Kiswahili"),
        AppLanguage("ta", "தமிழ்"),
        AppLanguage("te", "తెలుగు"),
        AppLanguage("th", "ไทย"),
        AppLanguage("tr", "Türkçe"),
        AppLanguage("uk", "Українська"),
        AppLanguage("ur", "اردو"),
        AppLanguage("vi", "Tiếng Việt"),
        AppLanguage("zh-CN", "简体中文"),
        AppLanguage("zh-TW", "繁體中文"),
    )

    fun labelFor(tag: String, systemLabel: String): String {
        if (tag.isBlank()) return systemLabel
        return all.firstOrNull { it.tag.equals(tag, ignoreCase = true) }?.nativeName ?: tag
    }
}
