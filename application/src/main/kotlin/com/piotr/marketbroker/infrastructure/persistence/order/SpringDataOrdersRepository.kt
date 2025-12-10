package com.piotr.marketbroker.infrastructure.persistence.order

import com.piotr.marketbroker.domain.order.Order
import org.springframework.data.repository.CrudRepository

interface SpringDataOrdersRepository: CrudRepository<Order, Int> {

    fun findByPositionId(positionId: Int): List<Order>

    fun findByOrderId(orderId: Int): List<Order>

    fun findOrdersByOrderIdIn(orderIds: List<Int>): List<Order>

    fun findOrdersByPositionIdIn(positionIds: List<Int>): List<Order>

    fun findOrdersByActiveTrue(): List<Order>
}
