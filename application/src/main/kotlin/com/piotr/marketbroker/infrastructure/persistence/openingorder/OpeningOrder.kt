package com.piotr.marketbroker.infrastructure.persistence.openingorder

import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.time.OffsetDateTime

@Entity
@Table(name = "openingorder")
class OpeningOrder (

    @Id @GeneratedValue var id: Int? = null,

    var orderID: Int,

    var currency: String,

    var currentPrice: Float,

    var direction: String,

    var expiryDate: OffsetDateTime,

    var goodTill: OffsetDateTime,

    var iDOLimitOrderPrice: String,

    var iDOStopOrderPrice: String,

    var iDOGuaranteed: Boolean,

    var isTriggered: Boolean,

    var limitOrderPrice: Float,

    var margin: Float,

    var market: String,

    var marketID: Int,

    var marketTradable: Boolean,

    var period: String,

    var creationTimeUTC: OffsetDateTime,

    var quoteId: Int,

    var quoteMode: String,

    var stake: Int,

    var status: Int,

    var stopOrderPrice: Float,

    var type: String,

    var trailingPoint: Int,

    var isGuarantee: Boolean,

    var isForceOpen: Boolean,

    var orderPriceModeEnum: String,

    var currencySymbol: String,

    var currencyCode: String

)