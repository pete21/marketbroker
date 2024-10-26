package com.piotr.marketbroker.infrastructure.persistence.order

import com.piotr.marketbroker.domain.order.Order
import org.springframework.data.repository.CrudRepository

interface SpringDataOrdersRepository: CrudRepository<Order, Int> {

    fun findByPositionId(positionId: Int): List<Order>

    fun findByOrderId(orderId: Int): List<Order>

}
