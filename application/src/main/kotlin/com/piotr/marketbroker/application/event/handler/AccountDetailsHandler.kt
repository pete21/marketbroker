package com.piotr.marketbroker.application.event.handler

import com.piotr.marketbroker.application.event.AccountDetailsEvent
import com.piotr.marketbroker.application.event.SessionClosedEvent
import com.piotr.marketbroker.application.mapper.AccountDetailsMapper.mapToPositionResponseDto
import com.piotr.marketbroker.application.mapper.AccountDetailsMapper.mapToOpeningOrderResponseDto
import com.piotr.marketbroker.application.model.OpeningOrderResponseDTO
import com.piotr.marketbroker.application.model.PositionResponseDTO
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
        if (event.openingOrders.isNotEmpty()) openingOrders = event.openingOrders.associateBy { it.orderId }
    }

    @Async
    @EventListener
    fun removeSubscriptions(event: SessionClosedEvent) {
        positions = mapOf()
        openingOrders = mapOf()
    }

    fun getPositions(): List<PositionResponseDTO> {
        return positions.values.map { mapToPositionResponseDto(it) }
    }

    fun positionExists(positionId: Int): Boolean {
        return positions.contains(positionId)
    }

    fun getPositionByPositionId(positionId: Int): PositionsRecord? {
        return positions[positionId]
    }

    fun getOpeningOrders(): List<OpeningOrderResponseDTO> {
        return openingOrders.values.map { mapToOpeningOrderResponseDto(it) }
    }

    fun getOpeningOrderByOrderId(orderID: Int): OpeningOrdersRecord? {
        return openingOrders[orderID]
    }
}
