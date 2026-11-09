package com.interveen.app.ui.paywall

import android.app.Activity
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.interveen.app.R
import com.interveen.app.data.billing.PlusBillingEvent
import com.interveen.app.domain.plus.PlusPhase
import com.interveen.app.domain.plus.PlusProducts
import com.interveen.app.ui.components.CalmButton
import com.interveen.app.ui.theme.CardCorner
import com.interveen.app.ui.theme.CoolGray
import com.interveen.app.ui.theme.CrispWhite
import com.interveen.app.ui.theme.Emerald
import com.interveen.app.ui.theme.EmeraldSoft
import com.interveen.app.ui.theme.QuietSlate
import com.interveen.app.ui.theme.SlateCharcoal
import com.interveen.app.ui.theme.SoftMist

@Composable
fun PaywallScreen(
    state: PaywallUiState,
    onSelectPlan: (String) -> Unit,
    onPurchase: (Activity) -> Unit,
    onRestore: () -> Unit,
    onClose: () -> Unit,
) {
    val activity = LocalContext.current as Activity
    val subscribed = state.phase == PlusPhase.Subscribed
    val required = state.phase == PlusPhase.Expired
    BackHandler(enabled = required) {}

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(SlateCharcoal)
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(horizontal = 24.dp, vertical = 20.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        if (!required) {
            TextButton(onClick = onClose, modifier = Modifier.align(Alignment.End)) {
                Text(
                    text = stringResource(R.string.paywall_not_now),
                    color = QuietSlate,
                )
            }
        }
        Text(
            text = stringResource(
                if (subscribed) R.string.paywall_subscribed_title else R.string.paywall_title,
            ),
            style = MaterialTheme.typography.titleLarge,
        )
        Text(
            text = stringResource(
                when (state.phase) {
                    PlusPhase.Subscribed -> R.string.paywall_subscribed
                    PlusPhase.Trial -> R.string.paywall_body
                    PlusPhase.Expired -> R.string.paywall_trial_ended
                    PlusPhase.Inactive -> R.string.paywall_body
                },
            ),
            style = MaterialTheme.typography.bodyMedium,
            color = SoftMist,
        )
        if (state.phase == PlusPhase.Trial && !state.trialEndLabel.isNullOrBlank()) {
            Text(
                text = stringResource(R.string.paywall_trial_active, state.trialEndLabel),
                style = MaterialTheme.typography.bodyMedium,
                color = Emerald,
            )
        }
        if (!subscribed && state.comparisonItem.isNotBlank()) {
            Text(
                text = stringResource(R.string.paywall_price_line, state.comparisonItem),
                style = MaterialTheme.typography.bodyMedium,
                color = QuietSlate,
            )
        }

        if (!subscribed) {
            PlanCard(
                title = stringResource(R.string.paywall_yearly),
                price = stringResource(R.string.paywall_yearly_price, state.yearlyPrice),
                badge = stringResource(R.string.paywall_best_value),
                selected = state.selectedProductId == PlusProducts.YEARLY,
                onClick = { onSelectPlan(PlusProducts.YEARLY) },
            )
            PlanCard(
                title = stringResource(R.string.paywall_monthly),
                price = stringResource(R.string.paywall_monthly_price, state.monthlyPrice),
                badge = null,
                selected = state.selectedProductId == PlusProducts.MONTHLY,
                onClick = { onSelectPlan(PlusProducts.MONTHLY) },
            )
            Spacer(Modifier.height(4.dp))
            CalmButton(
                text = stringResource(R.string.paywall_continue),
                onClick = { onPurchase(activity) },
                modifier = Modifier.fillMaxWidth(),
            )
        }
        TextButton(
            onClick = onRestore,
            modifier = Modifier.align(Alignment.CenterHorizontally),
        ) {
            Text(text = stringResource(R.string.paywall_restore), color = SoftMist)
        }
        state.notice?.let { notice ->
            val message = when (notice) {
                PlusBillingEvent.Failed -> stringResource(R.string.paywall_error)
                PlusBillingEvent.Restored -> stringResource(R.string.paywall_restored)
                PlusBillingEvent.NothingToRestore -> stringResource(R.string.paywall_nothing_to_restore)
                PlusBillingEvent.Purchased -> null
            }
            if (message != null) {
                Text(
                    text = message,
                    style = MaterialTheme.typography.bodyMedium,
                    color = SoftMist,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        }
        if (!subscribed) {
            Text(
                text = stringResource(R.string.paywall_fine_print),
                style = MaterialTheme.typography.labelMedium,
                color = QuietSlate,
            )
        }
    }
}

@Composable
private fun PlanCard(
    title: String,
    price: String,
    badge: String?,
    selected: Boolean,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(CardCorner))
            .background(if (selected) EmeraldSoft else CoolGray)
            .border(
                width = 1.dp,
                color = if (selected) Emerald else CoolGray,
                shape = RoundedCornerShape(CardCorner),
            )
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        Box(
            modifier = Modifier
                .size(20.dp)
                .border(width = 1.5.dp, color = if (selected) Emerald else QuietSlate, shape = CircleShape),
            contentAlignment = Alignment.Center,
        ) {
            if (selected) {
                Box(
                    modifier = Modifier
                        .size(10.dp)
                        .clip(CircleShape)
                        .background(Emerald),
                )
            }
        }
        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Text(text = title, style = MaterialTheme.typography.titleMedium)
            if (badge != null) {
                Text(
                    text = badge,
                    style = MaterialTheme.typography.labelMedium,
                    color = Emerald,
                )
            }
        }
        Text(
            text = price,
            style = MaterialTheme.typography.titleMedium,
            color = CrispWhite,
            textAlign = TextAlign.End,
        )
    }
}
