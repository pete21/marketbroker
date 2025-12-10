package com.piotr.marketbroker.domain.accountdetails

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import java.time.LocalDateTime
import java.util.*

data class AccountDetailsPositionsAttribute(
    @JsonProperty("Status")
    val status: Int,

    @JsonProperty("TotalRecords")
    val totalRecords: Int,

    @JsonProperty("Records")
    val records: ArrayList<PositionsRecord>
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class PositionsRecord (
    @JsonProperty("PositionID")
    val positionId: Int,

    @JsonProperty("OrderID")
    val orderId: Int,

    @JsonProperty("MarketID")
    val marketId: Int,

    @JsonProperty("QuoteID")
    val quoteId: Int,

    //    @JsonProperty("CurrencySymbol")
    //    String currencySymbol;
    @JsonProperty("Type")
    val type: String,

    //    @JsonProperty("MarketName")
    //    @JsonIgnore
    //    String marketName;
    @JsonProperty("Direction")
    val direction: String,

    //    @JsonProperty("ExpiryDateTime")
    //    @JsonIgnore
    //    String expiryDateTime;
    //    @JsonProperty("CreationTime")
//        @JsonIgnore
//        String creationTime;
    @JsonProperty("CreationTimeUTC")
    val creationTimeUTC: LocalDateTime,

    @JsonProperty("Stake")
    val stake: Float,

    //    @JsonProperty("OpeningPrice")
    //    @JsonIgnore
    //    String openingPrice;
    @JsonProperty("OpeningPriceDecimal")
    val openingPriceDecimal: Float,

    //    @JsonProperty("CurrentPrice")
    //    @JsonIgnore
    //    String currentPrice;
    @JsonProperty("CurrentPriceDecimal")
    val currentPriceDecimal: Float,

    @JsonProperty("OpenPL")
    val openPl: Int,

    @JsonProperty("StopOrderPrice")
    val stopOrderPrice: String,

    @JsonProperty("LimitOrderPrice")
    val limitOrderPrice: String,

    @JsonProperty("IMR")
    val imr: Float,

    @JsonProperty("PrcGenDecimalPlaces")
    val prcGenDecimalPlaces: Int,

    @JsonProperty("BetPer")
    val betPer: Float,

    @JsonProperty("Tradable")
    val tradable: Boolean,

    @JsonProperty("IsRollingMarket")
    val isRollingMarket: Boolean,

    @JsonProperty("IsTriggered")
    val isTriggered: Boolean,

    @JsonProperty("CurrencyCode")
    val currencyCode: String,

    @JsonProperty("IsTotal")
    val isTotal: Boolean
)