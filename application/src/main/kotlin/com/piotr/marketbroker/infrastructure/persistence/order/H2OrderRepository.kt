package com.piotr.marketbroker.infrastructure.persistence.order

import com.piotr.marketbroker.domain.order.Order
import com.piotr.marketbroker.domain.order.port.OrdersRepository
import org.springframework.stereotype.Repository

@Repository
class H2OrderRepository(
    private val springDataOrdersRepository: SpringDataOrdersRepository
) : OrdersRepository {

    override fun save(order: Order): Order {
        return springDataOrdersRepository.save(order)
    }

    override fun findAll(): List<Order> {
        return springDataOrdersRepository.findAll().toList()
    }

    override fun findByOrderId(orderId: Int): Order? {
        return springDataOrdersRepository.findByOrderId(orderId).first()
    }

}
