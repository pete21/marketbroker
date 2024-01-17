package com.piotr.marketbroker.infrastructure.persistence.openingorder

import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.time.OffsetDateTime

@Entity
@Table(name = "OpeningOrder")
data class OpeningOrder (

    @Id
    val id: Int,

    val orderID: Int,

    val currency: String,

    val currentPrice: Float,

    val direction: String,

    val expiryDate: OffsetDateTime,

    val goodTill: OffsetDateTime,

    val iDOLimitOrderPrice: String,

    val iDOStopOrderPrice: String,

    val iDOGuaranteed: Boolean,

    val isTriggered: Boolean,

    val limitOrderPrice: Float,

    val margin: Float,

    val market: String,

    val marketID: Int,

    val marketTradable: Boolean,

    val period: String,

    val creationTimeUTC: OffsetDateTime,

    val quoteId: Int,

    val quoteMode: String,

    val stake: Int,

    val status: Int,

    val stopOrderPrice: Float,

    val type: String,

    val trailingPoint: Int,

    val isGuarantee: Boolean,

    val isForceOpen: Boolean,

    val orderPriceModeEnum: String,

    val currencySymbol: String,

    val currencyCode: String

)
