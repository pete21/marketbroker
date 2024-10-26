package com.piotr.marketbroker.application.websocket.message

import com.fasterxml.jackson.annotation.JsonProperty

data class WebsocketTicks(
    @JsonProperty("sp")
    val sp: ArrayList<String>
)
