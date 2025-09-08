package com.piotr.marketbroker.infrastructure.persistence.mttrades

import com.piotr.marketbroker.domain.copytrade.CopyTrade
import org.springframework.data.repository.CrudRepository

interface SpringDataCopyTradeRepository: CrudRepository<CopyTrade, Int> {

    fun findByTradeId(tradeId: Int): List<CopyTrade>

}
