package com.piotr.marketbroker.application.event.handler

import com.piotr.marketbroker.application.event.SessionClosedEvent
import com.piotr.marketbroker.application.event.SubscriptionEvent
import com.piotr.marketbroker.infrastructure.persistence.keys.KeysRepository
import com.piotr.marketbroker.infrastructure.persistence.subscription.SpringDataSubscriptionsRepository
import com.piotr.marketbroker.infrastructure.persistence.subscription.Subscription
import mu.KotlinLogging
import org.springframework.context.event.EventListener
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Service

private val log = KotlinLogging.logger(SubscriptionEventHandler::class.toString())

@Service
class SubscriptionEventHandler (
    private val subscriptionsRepository: SpringDataSubscriptionsRepository,
    private val keysRepository: KeysRepository
) {

    @Async
    @EventListener
    fun handleSubscriptionEvent(event: SubscriptionEvent) {

        when (event.action) {
            "subscribe" -> {
                val s = Subscription(event.quoteId, true)
                subscriptionsRepository.save(s)
                log.info("Subscriptions updated")
            }

            "unsubscribe" -> {
                keysRepository.remove(event.quoteId)
                try {
                    val s = subscriptionsRepository.findById(event.quoteId).orElseThrow()
                    if (!s.status) {
                        log.warn("Instrument ${event.quoteId} not subscribed to.")
                    } else {
                        s.status=false
                        subscriptionsRepository.save(s)
                        log.info("Subscriptions updated")
                    }
                } catch (e: Exception) {
                    log.error("Unsubscribe error: $e")
                }
            }
        }
    }

    @Async
    @EventListener
    fun removeSubscriptions(event: SessionClosedEvent) {
        subscriptionsRepository.deleteAll()
    }

}
