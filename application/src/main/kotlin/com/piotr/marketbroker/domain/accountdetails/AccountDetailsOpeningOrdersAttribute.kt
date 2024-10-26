package com.piotr.marketbroker.domain.accountdetails

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonProperty
import java.time.LocalDateTime
import java.util.ArrayList


data class AccountDetailsOpeningOrdersAttribute(
    @JsonProperty("Status")
    val status: Int,

    @JsonProperty("TotalRecords")
    val totalRecords: Int,

    @JsonProperty("Records")
    val records: ArrayList<OpeningOrdersRecord>
)


data class OpeningOrdersRecord (

    @JsonProperty("Currency")
    private val currency: String,

    @JsonProperty("CurrentPrice")
    val currentPrice: Float,

    @JsonProperty("Direction")
    val direction: String,

    @JsonProperty("ExpiryDate")
    val expiryDate: String,

    @JsonProperty("GoodTill")
    val goodTill: String,

    @JsonProperty("IDOLimitOrderPrice")
    val iDOLimitOrderPrice: String,

    @JsonProperty("IDOStopOrderPrice")
    val iDOStopOrderPrice: String,

    @JsonProperty("IDOGuaranteed")
    val iDOGuaranteed: Boolean,

    @JsonProperty("IsTriggered")
    val isTriggered: Boolean,

    @JsonProperty("LimitOrderPrice")
    val limitOrderPrice: String,

    @JsonProperty("Margin")
    val margin: Float,

    @JsonProperty("Market")
    val market: String,

    @JsonProperty("MarketID")
    val marketId: Int,

    @JsonProperty("MarketTradable")
    val marketTradable: Boolean,

    @JsonProperty("OrderID")
    val orderId: Int,

    @JsonProperty("Period")
    val period: String,

    @JsonProperty("CreationTimeUTC")
    val creationTimeUTC: LocalDateTime,

    @JsonProperty("QuoteId")
    val quoteId: Int,

    @JsonProperty("QuoteMode")
    val quoteMode: String,

    @JsonProperty("Stake")
    val stake: Int,

    @JsonProperty("Status")
    val status: Int,

    @JsonProperty("StopOrderPrice")
    val stopOrderPrice: String,

    @JsonProperty("Type")
    val type: String,

    @JsonProperty("TrailingPoint")
    val trailingPoint: Int,

    @JsonProperty("IsGuarantee")
    val isGuarantee: Boolean,

    @JsonProperty("IsForceOpen")
    val isForceOpen: Boolean,

    @JsonProperty("OrderPriceModeEnum")
    val orderPriceModeEnum: String,

    @JsonIgnore
    @JsonProperty("CurrencySymbol")
    val currencySymbol: String,

    @JsonProperty("CurrencyCode")
    val currencyCode: String,

    var active: Boolean = true
)
