package com.piotr.marketbroker.infrastructure.persistence.order

import org.springframework.data.repository.CrudRepository

interface SpringDataOrdersRepository: CrudRepository<Order, Int>
