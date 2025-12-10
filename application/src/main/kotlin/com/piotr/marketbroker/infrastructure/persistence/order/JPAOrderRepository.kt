package com.piotr.marketbroker.infrastructure.persistence.order

import com.piotr.marketbroker.domain.order.Order
import com.piotr.marketbroker.domain.order.port.OrdersRepository
import org.springframework.stereotype.Repository

@Repository
class JPAOrderRepository(
    private val springDataOrdersRepository: SpringDataOrdersRepository
) : OrdersRepository {

    override fun save(order: Order): Order {
        return springDataOrdersRepository.save(order)
    }

    override fun findAll(): List<Order> {
        return springDataOrdersRepository.findAll().toList()
    }

    override fun findOrdersByOrderIdIn(orderIds: List<Int>): List<Order> {
        return springDataOrdersRepository.findOrdersByOrderIdIn(orderIds)
    }

    override fun findByOrderId(orderId: Int): Order? {
        return springDataOrdersRepository.findByOrderId(orderId).first()
    }

    override fun findByPositionId(positionId: Int): List<Order> {
        return springDataOrdersRepository.findByPositionId(positionId)
    }

    override fun findOrdersByPositionIdIn(positionIds: List<Int>): List<Order> {
        return springDataOrdersRepository.findOrdersByPositionIdIn(positionIds)
    }

    override fun findOrdersByActiveTrue(): List<Order> {
        return springDataOrdersRepository.findOrdersByActiveTrue()
    }

}
