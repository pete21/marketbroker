package com.piotr.marketbroker.infrastructure.persistence.subscription

import com.piotr.marketbroker.domain.subscription.Subscription
import com.piotr.marketbroker.domain.subscription.port.SubscriptionRepository
import org.springframework.stereotype.Repository

@Repository
class JPASubscriptionRepository(
    private val springDataSubscriptionRepository: SpringDataSubscriptionRepository
) : SubscriptionRepository {
    override fun save(s: Subscription): Subscription {
        return springDataSubscriptionRepository.save(s)
    }

    override fun findById(subscriptionId: Int): Subscription? {
        return springDataSubscriptionRepository.findById(subscriptionId).orElse(null)
    }

    override fun deleteAll() {
        springDataSubscriptionRepository.deleteAll()
    }

    override fun findByStatusTrue(): List<Subscription> {
        return springDataSubscriptionRepository.findByStatusTrue()
    }

}
