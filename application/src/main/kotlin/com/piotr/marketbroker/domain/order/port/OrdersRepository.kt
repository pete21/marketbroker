package com.piotr.marketbroker.domain.order.port

import com.piotr.marketbroker.domain.order.Order

interface OrdersRepository {

    fun findAll(): List<Order>

    fun findByOrderId(orderId: Int): Order?

    fun save(order: Order): Order

}
