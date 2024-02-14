package com.piotr.marketbroker.infrastructure.persistence.accountdetailsopeningorder

import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.time.LocalDateTime

@Entity
@Table(name = "openingorder")
class AccountDetailsOpeningOrder (

    @Id @GeneratedValue val id: Int=0,

    val orderID: Int=0,

    val currency: String="",

    val currentPrice: Float=0f,

    val direction: String="",

    val expiryDate: LocalDateTime = LocalDateTime.now(),

    val goodTill: LocalDateTime = LocalDateTime.now(),

    val iDOLimitOrderPrice: String="",

    val iDOStopOrderPrice: String="",

    val iDOGuaranteed: Boolean=false,

    val isTriggered: Boolean=false,

    val limitOrderPrice: Float=0f,

    val margin: Float=0f,

    val market: String="",

    val marketID: Int=0,

    val marketTradable: Boolean=false,

    val period: String="",

    val creationTimeUTC: LocalDateTime = LocalDateTime.now(),

    val quoteId: Int=0,

    val quoteMode: String="",

    val stake: Int=0,

    val status: Int=0,

    val stopOrderPrice: Float=0f,

    val type: String="",

    val trailingPoint: Int=0,

    val isGuarantee: Boolean=false,

    val isForceOpen: Boolean=false,

    val orderPriceModeEnum: String="",

    val currencySymbol: String="",

    val currencyCode: String=""

)