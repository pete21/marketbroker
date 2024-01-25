package com.piotr.marketbroker.application.event.handler

import com.piotr.marketbroker.application.event.SubscriptionEvent
import com.piotr.marketbroker.infrastructure.persistence.keys.KeysRepository
import com.piotr.marketbroker.infrastructure.persistence.subscription.SpringDataSubscriptionsRepository
import com.piotr.marketbroker.infrastructure.persistence.subscription.Subscription
import mu.KotlinLogging
import org.springframework.context.event.EventListener

private val log = KotlinLogging.logger {}

class SubscriptionEventHandler (
    private val subscriptionsRepository: SpringDataSubscriptionsRepository,
    private val keysRepository: KeysRepository
) {
    @EventListener
    fun HandleSubscriptionEvent(event: SubscriptionEvent) {
        if (!event.status) {
            return
        }
        //TODO: check condition

        when (event.action) {
            "subscribe" -> {
                val s = Subscription(event.quoteId, true)
                subscriptionsRepository.save(s)
            }

            "unsubscribe" -> {
                keysRepository.remove(event.quoteId)
                try {
                    val s = subscriptionsRepository.findById(event.quoteId).orElseThrow()
                    if (!s.status) {
                        log.warn("Unsubscribe warning: Instrument {} not subscribed to.", event.quoteId)
                    } else {
                        s.status=false
                        subscriptionsRepository.save(s)
                    }
                } catch (e: Exception) {
                    log.error("Unsubscribe error: {}", e)
                }
            }
        }
    }


}