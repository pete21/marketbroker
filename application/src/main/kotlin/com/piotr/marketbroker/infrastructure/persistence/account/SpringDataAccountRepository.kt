package com.piotr.marketbroker.infrastructure.persistence.account

import org.springframework.data.jpa.repository.JpaRepository

interface SpringDataAccountRepository: JpaRepository<Account, Int>
