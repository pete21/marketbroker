package com.piotr.marketbroker.infrastructure.persistence.position

import org.springframework.data.jpa.repository.JpaRepository

interface SpringDataPositionRepository: JpaRepository<Position, Int>
