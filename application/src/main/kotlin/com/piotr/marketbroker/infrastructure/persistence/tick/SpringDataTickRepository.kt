package com.piotr.marketbroker.infrastructure.persistence.tick

import com.piotr.marketbroker.domain.tick.Tick
import org.springframework.data.repository.CrudRepository

interface SpringDataTickRepository : CrudRepository<Tick, Int>