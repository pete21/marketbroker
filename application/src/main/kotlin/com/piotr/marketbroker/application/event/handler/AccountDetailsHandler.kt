package com.piotr.marketbroker.application.event.handler

import com.piotr.marketbroker.application.event.AccountDetailsEvent
import com.piotr.marketbroker.application.websocket.message.OpeningOrdersRecord
import com.piotr.marketbroker.application.websocket.message.PositionsRecord
import org.springframework.context.event.EventListener
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Service

@Service
class AccountDetailsHandler {
    private var positions: Map<Int, PositionsRecord> = mapOf()
    private var openingOrders: Map<Int, OpeningOrdersRecord> = mapOf()

    @Async
    @EventListener
    fun handleAccountDetailsEvent(event: AccountDetailsEvent) {
        if (event.positions.isNotEmpty()) positions = event.positions.associateBy { it.positionId }
        if (event.openingOrders.isNotEmpty()) openingOrders = event.openingOrders.associateBy { it.orderID }
    }

    fun GetPositions(): List<PositionsRecord> {
        return positions.values.toList()
    }

    fun GetPositionByPositionId(positionID: Int): PositionsRecord? {
        return positions[positionID]
    }

    fun GetOrders(): List<OpeningOrdersRecord> {
        return openingOrders.values.toList()
    }

    fun GetOpeningOrderByOrderId(orderID: Int): OpeningOrdersRecord? {
        return openingOrders[orderID]
    }
}
