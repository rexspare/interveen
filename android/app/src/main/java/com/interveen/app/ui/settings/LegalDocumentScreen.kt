package com.interveen.app.ui.settings

import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.interveen.app.R
import com.interveen.app.core.SiteLinks
import com.interveen.app.ui.theme.SlateCharcoal
import com.interveen.app.ui.theme.SoftMist

enum class LegalDocument(val url: String, val titleRes: Int) {
    HOME(SiteLinks.HOME, R.string.settings_website),
    PRIVACY(SiteLinks.PRIVACY, R.string.settings_privacy),
    TERMS(SiteLinks.TERMS, R.string.settings_terms),
}

@Composable
fun LegalDocumentScreen(
    document: LegalDocument,
    onBack: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(SlateCharcoal)
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(horizontal = 20.dp, vertical = 16.dp),
    ) {
        Text(
            text = stringResource(R.string.settings_back),
            style = MaterialTheme.typography.labelLarge,
            color = SoftMist,
            modifier = Modifier.clickable(onClick = onBack),
        )
        Text(
            text = stringResource(document.titleRes),
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(top = 16.dp, bottom = 12.dp),
        )
        AndroidView(
            factory = { context ->
                WebView(context).apply {
                    setBackgroundColor(android.graphics.Color.parseColor("#0F172A"))
                    webViewClient = WebViewClient()
                    settings.javaScriptEnabled = true
                    settings.domStorageEnabled = true
                    loadUrl(document.url)
                }
            },
            modifier = Modifier.fillMaxSize(),
        )
    }
}
