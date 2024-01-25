package com.piotr.marketbroker.infrastructure.persistence.marketgroup

import org.springframework.data.repository.CrudRepository

interface SpringDataMarketGroupRepository: CrudRepository<MarketGroup, Int>