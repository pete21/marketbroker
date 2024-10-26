package com.piotr.marketbroker.domain.tick.port

import com.piotr.marketbroker.domain.tick.Tick

interface TickRepository {

    fun saveAll(tickData: List<Tick>): List<Tick>

}
