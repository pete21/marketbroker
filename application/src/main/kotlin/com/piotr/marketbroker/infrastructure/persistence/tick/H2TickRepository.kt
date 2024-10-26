package com.piotr.marketbroker.infrastructure.persistence.tick

import com.piotr.marketbroker.domain.tick.Tick
import com.piotr.marketbroker.domain.tick.port.TickRepository
import org.springframework.stereotype.Repository

@Repository
class H2TickRepository(
    private val springDataTickRepository: SpringDataTickRepository
) : TickRepository {

    override fun saveAll(tickData: List<Tick>): List<Tick> {
        return springDataTickRepository.saveAll(tickData).toList()
    }

}
