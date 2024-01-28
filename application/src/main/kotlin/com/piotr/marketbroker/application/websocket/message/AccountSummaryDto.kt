package com.piotr.marketbroker.application.websocket.message

import com.fasterxml.jackson.annotation.JsonProperty

data class AccountSummaryDto (
    @JsonProperty("AccountID")
    val accountID: String?,

    @JsonProperty("PlatformID")
    val platformID: Int,

    @JsonProperty("AccountValuation")
    val accountValuation: Float?,

    @JsonProperty("FundedPercentageString")
    val fundedPercentageString: String,

    @JsonProperty("ClientId")
    val clientId: Int,

    @JsonProperty("TradingAccountType")
    val tradingAccountType: String,

    @JsonProperty("Margin")
    val margin: Int?,

    @JsonProperty("OpenPnLQuote")
    val openPnLQuote: Int?,

    @JsonProperty("AccountBalance")
    val accountBalance: Float?,

    @JsonProperty("Credit")
    val credit: Float?,

    @JsonProperty("WaivedMargin")
    val waivedMargin: Float?,

    @JsonProperty("Resources")
    val resources: Float?,

    @JsonProperty("ChangeIMR")
    val changeIMR: Float?,

    @JsonProperty("VariationMarginRequired")
    private val variationMarginRequired: Float?,

    @JsonProperty("MarginPercent")
    val marginPercent: Float?,

    @JsonProperty("CalculatedUTCTicks")
    val calculatedUTCTicks: Long?,

    @JsonProperty("HasError")
    val hasError: Boolean?

)