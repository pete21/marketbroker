package com.piotr.marketbroker.application.websocket.message

import com.fasterxml.jackson.annotation.JsonIgnore

data class WebsocketMessage(
    val t: String,
    @JsonIgnore
    var d: String?,
    @JsonIgnore
    val cid: String?

)