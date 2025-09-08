package com.piotr.marketbroker.domain.copytrade.port

import com.piotr.marketbroker.domain.copytrade.CopyTrade

interface CopyTradeRepository {

    fun findAll(): List<CopyTrade>

    fun findByTradeId(tradeId: Int): CopyTrade?

    fun save(order: CopyTrade): CopyTrade

}
