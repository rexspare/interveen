package com.interveen.app.data.billing

import android.app.Activity
import android.content.Context
import com.android.billingclient.api.AcknowledgePurchaseParams
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.BillingClientStateListener
import com.android.billingclient.api.BillingFlowParams
import com.android.billingclient.api.BillingResult
import com.android.billingclient.api.PendingPurchasesParams
import com.android.billingclient.api.ProductDetails
import com.android.billingclient.api.Purchase
import com.android.billingclient.api.QueryProductDetailsParams
import com.android.billingclient.api.QueryPurchasesParams
import com.interveen.app.domain.plus.PlusProducts
import com.interveen.app.domain.repository.PreferencesRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext

enum class PlusBillingEvent {
    Purchased,
    Failed,
    Restored,
    NothingToRestore,
}

@Singleton
class PlayBillingRepository @Inject constructor(
    @ApplicationContext context: Context,
    private val preferencesRepository: PreferencesRepository,
) {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)
    private val gate = Mutex()
    private val productDetails = mutableMapOf<String, ProductDetails>()

    private val _prices = MutableStateFlow<Map<String, String>>(emptyMap())
    val prices: StateFlow<Map<String, String>> = _prices.asStateFlow()

    private val _events = MutableSharedFlow<PlusBillingEvent>(extraBufferCapacity = 1)
    val events: SharedFlow<PlusBillingEvent> = _events.asSharedFlow()

    private val client: BillingClient = BillingClient.newBuilder(context)
        .setListener { result, purchases ->
            scope.launch { onPurchasesUpdated(result, purchases) }
        }
        .enablePendingPurchases(
            PendingPurchasesParams.newBuilder().enableOneTimeProducts().build(),
        )
        .enableAutoServiceReconnection()
        .build()

    fun refresh() {
        scope.launch { refreshInternal(announceRestore = false) }
    }

    fun restore() {
        scope.launch { refreshInternal(announceRestore = true) }
    }

    fun purchase(activity: Activity, productId: String) {
        scope.launch {
            val ready = ensureReady()
            val details = productDetails[productId]
            val offerToken = details?.subscriptionOfferDetails
                ?.firstOrNull { it.offerId == null }
                ?.offerToken
                ?: details?.subscriptionOfferDetails?.firstOrNull()?.offerToken
            if (!ready || details == null || offerToken == null) {
                _events.emit(PlusBillingEvent.Failed)
                return@launch
            }
            val params = BillingFlowParams.newBuilder()
                .setProductDetailsParamsList(
                    listOf(
                        BillingFlowParams.ProductDetailsParams.newBuilder()
                            .setProductDetails(details)
                            .setOfferToken(offerToken)
                            .build(),
                    ),
                )
                .build()
            val result = withContext(Dispatchers.Main.immediate) {
                client.launchBillingFlow(activity, params)
            }
            if (result.responseCode != BillingClient.BillingResponseCode.OK) {
                _events.emit(PlusBillingEvent.Failed)
            }
        }
    }

    private suspend fun refreshInternal(announceRestore: Boolean) {
        gate.withLock {
            if (!ensureReady()) {
                if (announceRestore) _events.emit(PlusBillingEvent.Failed)
                return
            }
            queryProducts()
            val active = queryActivePurchases()
            if (active != null) {
                preferencesRepository.setPlusSubscribed(active)
            }
            if (announceRestore) {
                _events.emit(
                    when (active) {
                        true -> PlusBillingEvent.Restored
                        false -> PlusBillingEvent.NothingToRestore
                        null -> PlusBillingEvent.Failed
                    },
                )
            }
        }
    }

    private suspend fun onPurchasesUpdated(result: BillingResult, purchases: List<Purchase>?) {
        when (result.responseCode) {
            BillingClient.BillingResponseCode.OK -> {
                val active = applyPurchases(purchases.orEmpty())
                if (active) {
                    preferencesRepository.setPlusSubscribed(true)
                    _events.emit(PlusBillingEvent.Purchased)
                }
            }
            BillingClient.BillingResponseCode.USER_CANCELED -> Unit
            else -> _events.emit(PlusBillingEvent.Failed)
        }
    }

    private suspend fun ensureReady(): Boolean {
        if (client.isReady) return true
        val deferred = CompletableDeferred<Boolean>()
        client.startConnection(object : BillingClientStateListener {
            override fun onBillingSetupFinished(result: BillingResult) {
                if (!deferred.isCompleted) {
                    deferred.complete(result.responseCode == BillingClient.BillingResponseCode.OK)
                }
            }

            override fun onBillingServiceDisconnected() = Unit
        })
        return deferred.await()
    }

    private suspend fun queryProducts() {
        val products = PlusProducts.all.map { id ->
            QueryProductDetailsParams.Product.newBuilder()
                .setProductId(id)
                .setProductType(BillingClient.ProductType.SUBS)
                .build()
        }
        val params = QueryProductDetailsParams.newBuilder()
            .setProductList(products)
            .build()
        val deferred = CompletableDeferred<List<ProductDetails>>()
        client.queryProductDetailsAsync(params) { result, queryResult ->
            if (!deferred.isCompleted) {
                deferred.complete(
                    if (result.responseCode == BillingClient.BillingResponseCode.OK) {
                        queryResult.productDetailsList
                    } else {
                        emptyList()
                    },
                )
            }
        }
        val listed = deferred.await()
        productDetails.clear()
        listed.forEach { productDetails[it.productId] = it }
        _prices.value = listed.associate { details ->
            val price = details.subscriptionOfferDetails
                ?.firstOrNull { it.offerId == null }
                ?.pricingPhases
                ?.pricingPhaseList
                ?.lastOrNull()
                ?.formattedPrice
                ?: details.subscriptionOfferDetails
                    ?.firstOrNull()
                    ?.pricingPhases
                    ?.pricingPhaseList
                    ?.lastOrNull()
                    ?.formattedPrice
                ?: ""
            details.productId to price
        }.filterValues { it.isNotBlank() }
    }

    private suspend fun queryActivePurchases(): Boolean? {
        val params = QueryPurchasesParams.newBuilder()
            .setProductType(BillingClient.ProductType.SUBS)
            .build()
        val deferred = CompletableDeferred<List<Purchase>?>()
        client.queryPurchasesAsync(params) { result, purchases ->
            if (!deferred.isCompleted) {
                deferred.complete(
                    if (result.responseCode == BillingClient.BillingResponseCode.OK) {
                        purchases
                    } else {
                        null
                    },
                )
            }
        }
        val purchases = deferred.await() ?: return null
        return applyPurchases(purchases)
    }

    private suspend fun applyPurchases(purchases: List<Purchase>): Boolean {
        var active = false
        purchases.forEach { purchase ->
            val isPlus = purchase.products.any { PlusProducts.isPlusProduct(it) }
            val purchased = purchase.purchaseState == Purchase.PurchaseState.PURCHASED &&
                !purchase.isSuspended &&
                !purchase.isSuspended
            if (isPlus && purchased) {
                active = true
                if (!purchase.isAcknowledged) {
                    val params = AcknowledgePurchaseParams.newBuilder()
                        .setPurchaseToken(purchase.purchaseToken)
                        .build()
                    client.acknowledgePurchase(params) { }
                }
            }
        }
        return active
    }
}
