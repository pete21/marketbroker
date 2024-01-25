package com.piotr.marketbroker.application.event.handler

import com.piotr.marketbroker.application.event.AccountDetailsEvent
import com.piotr.marketbroker.application.websocket.message.OpeningOrdersRecord
import com.piotr.marketbroker.application.websocket.message.PositionsRecord
import org.springframework.context.event.EventListener

class AccountDetailsHandler {
    private lateinit var positions: Map<Int, PositionsRecord>
    private lateinit var openingOrders: Map<Int, OpeningOrdersRecord>

    @EventListener
    fun HandleAccountDetailsEvent(event: AccountDetailsEvent) {
        positions = event.positions.associateBy { it.positionId }
        openingOrders = event.openingOrders.associateBy { it.orderID }
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
