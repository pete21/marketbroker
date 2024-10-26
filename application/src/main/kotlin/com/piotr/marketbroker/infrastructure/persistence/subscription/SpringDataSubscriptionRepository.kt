package com.piotr.marketbroker.infrastructure.persistence.subscription

import com.piotr.marketbroker.domain.subscription.Subscription
import org.springframework.data.repository.CrudRepository

interface SpringDataSubscriptionRepository : CrudRepository<Subscription, Int> {

    fun findByStatusTrue(): List<Subscription>

}