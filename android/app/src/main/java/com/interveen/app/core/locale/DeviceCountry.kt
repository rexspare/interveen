package com.interveen.app.core.locale

import android.content.Context
import android.content.res.Resources
import android.telephony.TelephonyManager
import java.util.Locale

object DeviceCountry {
    fun code(context: Context): String {
        val telephony = context.getSystemService(TelephonyManager::class.java)
        val network = telephony?.networkCountryIso.iso2()
        val sim = telephony?.simCountryIso.iso2()
        val system = Resources.getSystem().configuration.locales.get(0)?.country.iso2()
        val app = Locale.getDefault().country.iso2()
        return network ?: sim ?: system ?: app ?: ""
    }

    private fun String?.iso2(): String? {
        val code = this?.trim()?.uppercase(Locale.US).orEmpty()
        return code.takeIf { it.length == 2 && it.all { char -> char.isLetter() } }
    }
}
