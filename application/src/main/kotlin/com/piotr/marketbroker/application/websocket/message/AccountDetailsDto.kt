package com.piotr.marketbroker.application.websocket.message

import com.fasterxml.jackson.annotation.JsonProperty

data class AccountDetailsDto(

    @JsonProperty("ClientId")
    val clientId: Int,

    @JsonProperty("TradingAccountType")
    val tradingAccountType: String,

    @JsonProperty("OpeningOrders")
    val openingOrders: AccountDetailsOpeningOrdersAttribute?,

//    @JsonProperty("Currencies")
//    val currencies: Any?,

    @JsonProperty("Positions")
    val positions: AccountDetailsPositionsAttribute?,

//    @JsonProperty("Alerts")
//    val alerts: Any?,

//    @JsonIgnore
//    @JsonProperty("ClientLanguageId")
//    val clientLanguageId: Int,

//    @JsonIgnore
//    @JsonProperty("CalculatedUTCTicks")
//    val calculatedUtcTicks: Long
)

