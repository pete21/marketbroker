package com.piotr.marketbroker.infrastructure.persistence.marketgroup

import com.piotr.marketbroker.domain.marketgroup.MarketGroup
import org.springframework.data.repository.CrudRepository

interface SpringDataMarketGroupsRepository: CrudRepository<MarketGroup, Int>