package com.piotr.marketbroker.infrastructure.persistence.openingorder

import org.springframework.data.repository.CrudRepository

interface SpringDataOpeningOrderRepository: CrudRepository<OpeningOrder, Int>
