package com.piotr.marketbroker.infrastructure.persistence.mttrades

import com.piotr.marketbroker.domain.copytrade.CopyTrade
import com.piotr.marketbroker.domain.copytrade.port.CopyTradeRepository
import org.springframework.stereotype.Repository

@Repository
class JPACopyTradeRepository(
    private val springDataCopyTradeRepository: SpringDataCopyTradeRepository
) : CopyTradeRepository {

    override fun save(order: CopyTrade): CopyTrade {
        return springDataCopyTradeRepository.save(order)
    }

    override fun findAll(): List<CopyTrade> {
        return springDataCopyTradeRepository.findAll().toList()
    }
    override fun findByTradeId(tradeId: Int): CopyTrade? {
        return springDataCopyTradeRepository.findByTradeId(tradeId).first()
    }

}
