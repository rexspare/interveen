package com.interveen.app.domain.plus

object PlusProducts {
    const val MONTHLY = "interveen_plus_monthly"
    const val YEARLY = "interveen_plus_yearly"

    const val FALLBACK_MONTHLY_PRICE = "$4.99"
    const val FALLBACK_YEARLY_PRICE = "$29.99"

    val all = listOf(YEARLY, MONTHLY)

    fun isPlusProduct(productId: String): Boolean = productId in all
}
