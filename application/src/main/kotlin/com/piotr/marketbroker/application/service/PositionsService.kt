package com.piotr.marketbroker.application.service

import com.piotr.marketbroker.application.mapper.PositionMapper
import com.piotr.marketbroker.application.model.PositionResponseDTO
import com.piotr.marketbroker.infrastructure.persistence.accountdetailsposition.AccountDetailsPosition
import com.piotr.marketbroker.infrastructure.persistence.accountdetailsposition.SpringDataAccountDetailsPositionRepository
import org.springframework.stereotype.Service

@Service
class PositionsService(
    private val springDataAccountDetailsPositionRepository: SpringDataAccountDetailsPositionRepository
) {
    fun getPosition(): List<PositionResponseDTO> {

        val positions = springDataAccountDetailsPositionRepository.findAll()

        return positions.map { PositionMapper.mapToPositionResponseDto(it) }

    }

    fun getPosition(positionId: Int): AccountDetailsPosition? {
        return springDataAccountDetailsPositionRepository.findByPositionID(positionId)
    }

}
