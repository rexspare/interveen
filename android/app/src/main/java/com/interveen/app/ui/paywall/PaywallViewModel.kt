package com.interveen.app.ui.paywall

import android.app.Activity
import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.interveen.app.core.locale.DeviceCountry
import com.interveen.app.data.billing.PlayBillingRepository
import com.interveen.app.data.billing.PlusBillingEvent
import com.interveen.app.domain.plus.PlusAccess
import com.interveen.app.domain.plus.PlusPhase
import com.interveen.app.domain.plus.PlusProducts
import com.interveen.app.domain.plus.PriceComparisons
import com.interveen.app.domain.repository.PreferencesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class PaywallUiState(
    val phase: PlusPhase = PlusPhase.Inactive,
    val trialEndLabel: String? = null,
    val comparisonItem: String = "",
    val yearlyPrice: String = PlusProducts.FALLBACK_YEARLY_PRICE,
    val monthlyPrice: String = PlusProducts.FALLBACK_MONTHLY_PRICE,
    val selectedProductId: String = PlusProducts.YEARLY,
    val notice: PlusBillingEvent? = null,
)

@HiltViewModel
class PaywallViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val preferencesRepository: PreferencesRepository,
    private val billing: PlayBillingRepository,
) : ViewModel() {

    private val selected = MutableStateFlow(PlusProducts.YEARLY)
    private val comparisonItem = MutableStateFlow("")
    private val notice = MutableStateFlow<PlusBillingEvent?>(null)

    val uiState: StateFlow<PaywallUiState> = combine(
        preferencesRepository.preferences,
        billing.prices,
        selected,
        comparisonItem,
        notice,
    ) { prefs, prices, selectedId, item, currentNotice ->
        val now = System.currentTimeMillis()
        val phase = PlusAccess.phase(prefs.plusSubscribed, prefs.plusTrialStartedAtMillis, now)
        val ends = PlusAccess.trialEndsAtMillis(prefs.plusTrialStartedAtMillis)
        PaywallUiState(
            phase = phase,
            trialEndLabel = if (phase == PlusPhase.Trial) ends?.let(::formatPlusDate) else null,
            comparisonItem = item,
            yearlyPrice = prices[PlusProducts.YEARLY] ?: PlusProducts.FALLBACK_YEARLY_PRICE,
            monthlyPrice = prices[PlusProducts.MONTHLY] ?: PlusProducts.FALLBACK_MONTHLY_PRICE,
            selectedProductId = selectedId,
            notice = currentNotice,
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), PaywallUiState())

    init {
        billing.refresh()
        viewModelScope.launch {
            val index = preferencesRepository.advancePriceLine()
            comparisonItem.value = PriceComparisons.item(DeviceCountry.code(context), index)
        }
        viewModelScope.launch {
            billing.events.collect { event ->
                notice.value = if (event == PlusBillingEvent.Purchased) null else event
            }
        }
    }

    fun select(productId: String) {
        selected.value = productId
    }

    fun purchase(activity: Activity) {
        notice.value = null
        billing.purchase(activity, selected.value)
    }

    fun restore() {
        notice.value = null
        billing.restore()
    }
}
