package com.piotr.marketbroker.infrastructure.persistence.openingorder

import org.springframework.data.jpa.repository.JpaRepository

interface SpringDataOpeningOrderRepository: JpaRepository<OpeningOrder, Int>
