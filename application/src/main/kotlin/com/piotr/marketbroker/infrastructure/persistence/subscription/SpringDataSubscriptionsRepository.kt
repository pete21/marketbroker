package com.piotr.marketbroker.infrastructure.persistence.subscription

import org.springframework.data.repository.CrudRepository

interface SpringDataSubscriptionsRepository: CrudRepository<Subscription, Int> {

    fun findByStatusTrue() : List<Subscription>

    fun findByStatus(subscribed: Boolean) : List<Subscription>
}