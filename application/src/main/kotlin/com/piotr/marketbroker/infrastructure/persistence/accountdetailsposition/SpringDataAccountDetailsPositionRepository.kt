package com.piotr.marketbroker.infrastructure.persistence.accountdetailsposition

import org.springframework.data.jpa.repository.JpaRepository

interface SpringDataAccountDetailsPositionRepository: JpaRepository<AccountDetailsPosition, Int> {
    fun findByPositionID(positionId: Int): AccountDetailsPosition?
}
