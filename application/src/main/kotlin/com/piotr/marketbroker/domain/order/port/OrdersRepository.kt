package com.piotr.marketbroker.domain.order.port

import com.piotr.marketbroker.domain.order.Order

interface OrdersRepository {

    fun findAll(): List<Order>

    fun findOrdersByOrderIdIn(orderIds: List<Int>): List<Order>

    fun findByOrderId(orderId: Int): Order?

    fun findByPositionId(positionId: Int): List<Order>

    fun findOrdersByPositionIdIn(positionIds: List<Int>): List<Order>

    fun findOrdersByActiveTrue(): List<Order>

    fun save(order: Order): Order

}
