package com.piotr.marketbroker.infrastructure.rest.controller.mapper

import com.piotr.marketbroker.application.model.TickResponseDTO
import com.piotr.marketbroker.domain.tick.Tick

object TickMapper {
    fun mapTickToTickResponseDto(tick: Tick) = TickResponseDTO(
        quoteId = tick.quoteId,
        bid = tick.bid,
        ask = tick.ask,
        time = tick.time.toBigDecimal(),
        millis = tick.millis.toBigDecimal(),
    )

}