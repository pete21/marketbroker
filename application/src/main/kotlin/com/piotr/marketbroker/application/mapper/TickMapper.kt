package com.piotr.marketbroker.application.mapper

import com.piotr.marketbroker.application.event.TickData
import com.piotr.marketbroker.infrastructure.persistence.tick.Tick

object TickMapper {
    fun toTick(tickdata: List<TickData>) : List<Tick> {
    return tickdata.map { Tick(0, it.quoteId, it.bid, it.ask, it.mid, it.time, it.millis, it.longtime, it.key )}
    }

}
