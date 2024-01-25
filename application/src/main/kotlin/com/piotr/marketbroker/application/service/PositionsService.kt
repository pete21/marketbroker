package com.piotr.marketbroker.application.service

import com.piotr.marketbroker.application.mapper.PositionMapper
import com.piotr.marketbroker.application.model.PositionResponseDTO
import com.piotr.marketbroker.infrastructure.persistence.position.SpringDataPositionRepository
import org.springframework.stereotype.Service

@Service
class PositionsService(
    private val springDataPositionRepository: SpringDataPositionRepository
) {
    fun getPositions(): List<PositionResponseDTO> {

        val positions = springDataPositionRepository.findAll()

        return positions.map { PositionMapper.mapToPositionResponseDto(it) }

    }

}
