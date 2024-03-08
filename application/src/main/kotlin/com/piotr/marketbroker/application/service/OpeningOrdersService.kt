package com.piotr.marketbroker.application.service
/*
import com.piotr.marketbroker.application.mapper.OpeningOrderMapper
import com.piotr.marketbroker.application.model.OrderResponseDTO
import com.piotr.marketbroker.infrastructure.persistence.accountdetailsopeningorder.SpringDataAccountDetailsOpeningOrderRepository
import org.springframework.stereotype.Service

@Service
class OpeningOrdersService(
    private val springDataAccountDetailsOpeningOrderRepository: SpringDataAccountDetailsOpeningOrderRepository
) {
    fun getOrders(): List<OrderResponseDTO> {

        val openingOrders = springDataAccountDetailsOpeningOrderRepository.findAll()

        return openingOrders.map { OpeningOrderMapper.mapToOrderResponseDto(it) }

    }

}


 */