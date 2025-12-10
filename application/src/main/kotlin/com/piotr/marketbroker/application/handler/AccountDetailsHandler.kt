package com.piotr.marketbroker.application.handler

import com.piotr.marketbroker.application.event.AccountDetailsEvent
import com.piotr.marketbroker.application.event.MarketOrderExecutedEvent
import com.piotr.marketbroker.application.event.OpeningOrderExecutedEvent
import com.piotr.marketbroker.application.event.PositionClosedEvent
import com.piotr.marketbroker.application.event.SessionClosedEvent
import com.piotr.marketbroker.common.logger
import com.piotr.marketbroker.domain.accountdetails.OpeningOrdersRecord
import com.piotr.marketbroker.domain.accountdetails.PositionsRecord
import org.springframework.context.ApplicationEventPublisher
import org.springframework.context.event.EventListener
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Service

@Service
class AccountDetailsHandler(
    private val applicationEventPublisher: ApplicationEventPublisher
) {
    private var positions: Map<Int, PositionsRecord> = mapOf()
    private var openingOrders: Map<Int, OpeningOrdersRecord> = mapOf()

    private val log by logger()
//    @Async
    @EventListener
    fun handleAccountDetailsEvent(event: AccountDetailsEvent) {
        val eventPositions = event.positions.filterNot { it.isTotal }           //filter out aggregate position
        log.info("Number of event positions: ${eventPositions.size}")
        val previousPositions = positions.keys.toList()
        val currentPositions = eventPositions.map { it.positionId }
        val closedPositionIds = previousPositions.filterNot { it in currentPositions }
        val closedPositions = positions.filterKeys { it in closedPositionIds }.values.toList()
        val newPositions = eventPositions.filterNot { it.positionId in previousPositions }

        positions = eventPositions.associateBy { it.positionId }

        if (closedPositions.isNotEmpty()) {
            applicationEventPublisher.publishEvent(PositionClosedEvent(closedPositions))
        }

        if (newPositions.isNotEmpty()) {               // nowe pozycje, wsrod nich executed Market orders
            applicationEventPublisher.publishEvent(
                MarketOrderExecutedEvent(
                    newMarketPositions = newPositions
                )
            )
        }

        val previousOrders = openingOrders.keys.toList()
        val currentOrders = event.openingOrders.map { it.orderId }
        val executedOrderIds = previousOrders.filterNot { it in currentOrders }

        openingOrders = event.openingOrders.associateBy { it.orderId }

        if (executedOrderIds.isNotEmpty()) {               // jezeli wykonane OpeningOrders
            applicationEventPublisher.publishEvent(
                OpeningOrderExecutedEvent(
                    newPositions = newPositions,
                    executedOpeningOrderIds = executedOrderIds
                )
            )
        }

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
