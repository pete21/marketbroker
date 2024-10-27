package com.piotr.marketbroker.domain.accountdetails.handler

import com.piotr.marketbroker.domain.accountdetails.event.AccountDetailsEvent
import com.piotr.marketbroker.application.event.SessionClosedEvent
import com.piotr.marketbroker.domain.accountdetails.OpeningOrdersRecord
import com.piotr.marketbroker.domain.accountdetails.PositionsRecord
import com.piotr.marketbroker.domain.order.port.OrdersRepository
import org.springframework.context.event.EventListener
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Service

@Service
class AccountDetailsHandler(
    private val ordersRepository: OrdersRepository
) {
    private var positions: Map<Int, PositionsRecord> = mapOf()
    private var openingOrders: Map<Int, OpeningOrdersRecord> = mapOf()

    @Async
    @EventListener
    fun handleAccountDetailsEvent(event: AccountDetailsEvent) {
        if (event.positions.isNotEmpty()) positions = event.positions.associateBy { it.positionId }
        openingOrders.filterNot { it.value.orderId in event.openingOrders.map { it.orderId } }.values
            .forEach {
                it.active = false
                ordersRepository.findByOrderId(it.orderId)?.let {
                    it.active = false
                    ordersRepository.save(it)
                }
            }
        openingOrders = event.openingOrders.associateBy { it.orderId }
    }

    @Async
    @EventListener
    fun clearPositionsAndOrders(event: SessionClosedEvent) {
        positions = mapOf()
        openingOrders = mutableMapOf()
    }

    fun getPositions(): List<PositionsRecord> {
        return positions.values.toList()
    }

    fun positionExists(positionId: Int): Boolean {
        return positions.contains(positionId)
    }

    fun getPositionByPositionId(positionId: Int): PositionsRecord? {
        return positions[positionId]
    }

    fun getOpeningOrders(): List<OpeningOrdersRecord> {
        return openingOrders.values.toList()
    }

    fun getOpeningOrderByOrderId(orderID: Int): OpeningOrdersRecord? {
        return openingOrders[orderID]
    }
}
