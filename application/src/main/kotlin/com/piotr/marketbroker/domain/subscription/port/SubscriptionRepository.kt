package com.piotr.marketbroker.domain.subscription.port

import com.piotr.marketbroker.domain.subscription.Subscription

interface SubscriptionRepository {

    fun save(s: Subscription) : Subscription

    fun findById(subscriptionId: Int): Subscription?

    fun deleteAll()

    fun findByStatusTrue(): List<Subscription>

}
