package com.piotr.marketbroker.domain.marketgroup.port

import com.piotr.marketbroker.domain.marketgroup.MarketGroup

interface MarketGroupRepository {

    fun findAll(): List<MarketGroup>

    fun saveAll(marketGroups: List<MarketGroup>) : List<MarketGroup>

    fun findById(id: Int): MarketGroup?

}