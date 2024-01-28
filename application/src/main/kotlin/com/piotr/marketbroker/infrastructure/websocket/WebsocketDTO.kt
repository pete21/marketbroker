package com.piotr.marketbroker.infrastructure.websocket

import com.fasterxml.jackson.annotation.JsonIgnore

data class WebsocketDTO(
    val t: String,
    @JsonIgnore
    var d: String?,
    @JsonIgnore
    val cid: String?

)
