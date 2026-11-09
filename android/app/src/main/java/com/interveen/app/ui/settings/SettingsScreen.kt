package com.interveen.app.ui.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.automirrored.outlined.KeyboardArrowRight
import androidx.compose.material.icons.outlined.ExpandMore
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.unit.dp
import com.interveen.app.R
import com.interveen.app.core.locale.AppLanguage
import com.interveen.app.core.locale.AppLanguages
import com.interveen.app.data.billing.PlusBillingEvent
import com.interveen.app.domain.plus.PlusPhase
import com.interveen.app.ui.components.CalmButton
import com.interveen.app.ui.components.QuietCard
import com.interveen.app.ui.theme.CardCorner
import com.interveen.app.ui.theme.CoolGray
import com.interveen.app.ui.theme.CrispWhite
import com.interveen.app.ui.theme.Emerald
import com.interveen.app.ui.theme.QuietSlate
import com.interveen.app.ui.theme.Rule
import com.interveen.app.ui.theme.SlateCharcoal
import com.interveen.app.ui.theme.SoftMist

@Composable
fun SettingsScreen(
    state: SettingsUiState,
    onLanguageSelected: (String) -> Unit,
    onOpenWebsite: () -> Unit,
    onOpenPrivacy: () -> Unit,
    onOpenTerms: () -> Unit,
    onOpenPaywall: () -> Unit,
    onAddDomain: (String) -> Boolean,
    onAddKeyword: (String) -> Boolean,
    onRemoveDomain: (String) -> Unit,
    onRemoveKeyword: (String) -> Unit,
    onRestoreSubscription: () -> Unit,
    onRate: () -> Unit,
    onBack: () -> Unit,
) {
    val systemLabel = stringResource(R.string.settings_language_system)
    val languages = remember(systemLabel) {
        listOf(AppLanguage("", systemLabel)) + AppLanguages.all
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(SlateCharcoal)
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(horizontal = 24.dp, vertical = 20.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(18.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBack) {
                Icon(
                    imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                    contentDescription = stringResource(R.string.settings_back),
                    tint = CrispWhite,
                )
            }
            Text(
                text = stringResource(R.string.settings_title),
                style = MaterialTheme.typography.titleLarge,
            )
        }

        SettingsSection(title = stringResource(R.string.settings_section_plus)) {
            Text(
                text = when (state.plusPhase) {
                    PlusPhase.Subscribed -> stringResource(R.string.settings_plus_active)
                    PlusPhase.Trial -> stringResource(
                        R.string.settings_plus_trial,
                        state.trialEndLabel.orEmpty(),
                    )
                    PlusPhase.Expired, PlusPhase.Inactive -> stringResource(R.string.paywall_trial_ended)
                },
                style = MaterialTheme.typography.bodyMedium,
                color = if (state.plusPhase == PlusPhase.Subscribed) Emerald else SoftMist,
            )
            if (state.plusPhase != PlusPhase.Subscribed) {
                CalmButton(
                    text = stringResource(R.string.settings_subscribe),
                    onClick = onOpenPaywall,
                    modifier = Modifier.fillMaxWidth(),
                )
            }
            SettingsGroup {
                SettingsRow(
                    title = stringResource(R.string.paywall_restore),
                    onClick = onRestoreSubscription,
                )
            }
            state.restoreNotice?.let { notice ->
                val message = when (notice) {
                    PlusBillingEvent.Failed ->
                        stringResource(R.string.paywall_error)
                    PlusBillingEvent.Restored ->
                        stringResource(R.string.paywall_restored)
                    PlusBillingEvent.NothingToRestore ->
                        stringResource(R.string.paywall_nothing_to_restore)
                    PlusBillingEvent.Purchased -> null
                }
                if (message != null) {
                    Text(
                        text = message,
                        style = MaterialTheme.typography.bodyMedium,
                        color = SoftMist,
                    )
                }
            }
        }

        SettingsSection(title = stringResource(R.string.settings_section_list)) {
            if (state.hasPlus) {
                Text(
                    text = stringResource(R.string.settings_list_body),
                    style = MaterialTheme.typography.bodyMedium,
                    color = SoftMist,
                )
                EntryEditor(
                    hint = stringResource(R.string.settings_list_hint_domain),
                    button = stringResource(R.string.settings_list_add_domain),
                    entries = state.domains,
                    onAdd = onAddDomain,
                    onRemove = onRemoveDomain,
                )
                EntryEditor(
                    hint = stringResource(R.string.settings_list_hint_word),
                    button = stringResource(R.string.settings_list_add_word),
                    entries = state.keywords,
                    onAdd = onAddKeyword,
                    onRemove = onRemoveKeyword,
                )
            } else {
                QuietCard(modifier = Modifier.fillMaxWidth(), onClick = onOpenPaywall) {
                    Text(
                        text = stringResource(R.string.settings_list_locked),
                        style = MaterialTheme.typography.bodyMedium,
                        color = SoftMist,
                    )
                }
            }
        }

        SettingsSection(title = stringResource(R.string.settings_section_language)) {
            Text(
                text = stringResource(R.string.settings_language_body),
                style = MaterialTheme.typography.bodyMedium,
                color = SoftMist,
            )
            LanguageDropdown(
                selectedTag = state.languageTag,
                languages = languages,
                selectedLabel = AppLanguages.labelFor(state.languageTag, systemLabel),
                onLanguageSelected = onLanguageSelected,
            )
        }

        if (!state.playStoreRated) {
            SettingsSection(title = stringResource(R.string.settings_section_rate)) {
                SettingsGroup {
                    SettingsRow(
                        title = stringResource(R.string.settings_rate),
                        body = stringResource(R.string.settings_rate_body),
                        onClick = onRate,
                    )
                }
            }
        }

        SettingsSection(title = stringResource(R.string.settings_section_legal)) {
            SettingsGroup {
                SettingsRow(
                    title = stringResource(R.string.settings_website),
                    onClick = onOpenWebsite,
                )
                HorizontalDivider(color = Rule)
                SettingsRow(
                    title = stringResource(R.string.settings_privacy),
                    onClick = onOpenPrivacy,
                )
                HorizontalDivider(color = Rule)
                SettingsRow(
                    title = stringResource(R.string.settings_terms),
                    onClick = onOpenTerms,
                )
            }
        }
    }
}

@Composable
private fun SettingsSection(
    title: String,
    content: @Composable () -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Text(
            text = title,
            style = MaterialTheme.typography.labelLarge,
            color = QuietSlate,
        )
        content()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun LanguageDropdown(
    selectedTag: String,
    languages: List<AppLanguage>,
    selectedLabel: String,
    onLanguageSelected: (String) -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }
    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = it },
    ) {
        QuietCard(
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor(MenuAnchorType.PrimaryNotEditable),
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = selectedLabel,
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.weight(1f),
                )
                Icon(
                    imageVector = Icons.Outlined.ExpandMore,
                    contentDescription = null,
                    tint = SoftMist,
                )
            }
        }
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.heightIn(max = 360.dp),
        ) {
            languages.forEach { language ->
                DropdownMenuItem(
                    text = { Text(language.nativeName) },
                    onClick = {
                        onLanguageSelected(language.tag)
                        expanded = false
                    },
                    trailingIcon = {
                        if (language.tag.equals(selectedTag, ignoreCase = true) ||
                            (language.tag.isBlank() && selectedTag.isBlank())
                        ) {
                            Text("✓", color = MaterialTheme.colorScheme.primary)
                        }
                    },
                )
            }
        }
    }
}

@Composable
private fun EntryEditor(
    hint: String,
    button: String,
    entries: List<String>,
    onAdd: (String) -> Boolean,
    onRemove: (String) -> Unit,
) {
    var draft by remember { mutableStateOf("") }
    QuietCard(modifier = Modifier.fillMaxWidth()) {
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            BasicTextField(
                value = draft,
                onValueChange = { draft = it },
                singleLine = true,
                textStyle = MaterialTheme.typography.bodyLarge.copy(color = CrispWhite),
                cursorBrush = SolidColor(CrispWhite),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(onDone = {
                    if (onAdd(draft)) draft = ""
                }),
                modifier = Modifier.fillMaxWidth(),
                decorationBox = { inner ->
                    if (draft.isEmpty()) {
                        Text(text = hint, style = MaterialTheme.typography.bodyLarge, color = QuietSlate)
                    }
                    inner()
                },
            )
            TextButton(
                onClick = { if (onAdd(draft)) draft = "" },
                modifier = Modifier.align(Alignment.End),
            ) {
                Text(text = button, color = Emerald)
            }
            entries.forEach { entry ->
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = entry,
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.weight(1f),
                    )
                    TextButton(onClick = { onRemove(entry) }) {
                        Text(text = stringResource(R.string.settings_list_remove), color = Emerald)
                    }
                }
            }
        }
    }
}

@Composable
private fun SettingsGroup(content: @Composable () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(CardCorner))
            .background(CoolGray),
    ) {
        content()
    }
}

@Composable
private fun SettingsRow(
    title: String,
    onClick: () -> Unit,
    body: String? = null,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(2.dp),
        ) {
            Text(text = title, style = MaterialTheme.typography.titleMedium)
            if (body != null) {
                Text(
                    text = body,
                    style = MaterialTheme.typography.bodyMedium,
                    color = SoftMist,
                )
            }
        }
        Icon(
            imageVector = Icons.AutoMirrored.Outlined.KeyboardArrowRight,
            contentDescription = null,
            tint = QuietSlate,
        )
    }
}
