package com.piotr.marketbroker.infrastructure.persistence.marketgroup

import com.piotr.marketbroker.domain.marketgroup.MarketGroup
import com.piotr.marketbroker.domain.marketgroup.port.MarketGroupRepository
import org.springframework.stereotype.Repository

@Repository
class H2MarketGroupsRepository(
    private val springDataMarketGroupsRepository: SpringDataMarketGroupsRepository
) : MarketGroupRepository {
    override fun findAll(): List<MarketGroup> {
        return springDataMarketGroupsRepository.findAll().toList()
    }

    override fun saveAll(marketGroups: List<MarketGroup>): List<MarketGroup> {
        return springDataMarketGroupsRepository.saveAll(marketGroups).toList()
    }

    override fun findById(id: Int): MarketGroup? {
        return springDataMarketGroupsRepository.findById(id).orElse(null)
    }
}