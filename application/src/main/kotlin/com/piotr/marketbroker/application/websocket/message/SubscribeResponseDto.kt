package com.piotr.marketbroker.application.websocket.message

import com.fasterxml.jackson.annotation.JsonProperty

data class SubscribeResponseDto (
    @JsonProperty("QuoteId")
    val quoteId: Int,

    @JsonProperty("Current")
    val current: ArrayList<String>?,

    @JsonProperty("PriceGrouping")
    val priceGrouping: String,

    @JsonProperty("Result")
    val result: Boolean,

    @JsonProperty("Action")
    val action: String,

    @JsonProperty("HasError")
    val hasError: Boolean
)

