package com.piotr.marketbroker.domain.tick.port

import com.piotr.marketbroker.domain.tick.Tick

interface TickState {

    fun put(quoteId: Int, tick: Tick)
    fun get(quoteId: Int): Tick?
    fun remove(quoteId: Int)
    fun getAll(): List<Tick>

}
