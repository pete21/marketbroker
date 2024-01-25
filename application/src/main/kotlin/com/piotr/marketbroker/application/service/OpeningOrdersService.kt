package com.piotr.marketbroker.application.service

import com.piotr.marketbroker.application.mapper.OpeningOrderMapper
import com.piotr.marketbroker.application.model.OrderResponseDTO
import com.piotr.marketbroker.infrastructure.persistence.openingorder.SpringDataOpeningOrderRepository
import org.springframework.stereotype.Service

@Service
class OpeningOrdersService(
    private val springDataOpeningOrderRepository: SpringDataOpeningOrderRepository
) {
    fun getOrders(): List<OrderResponseDTO> {

        val openingOrders = springDataOpeningOrderRepository.findAll()

        return openingOrders.map { OpeningOrderMapper.mapToOrderResponseDto(it) }

    }

}
