package com.piotr.marketbroker.domain.subscription.handler

import com.piotr.marketbroker.application.event.SessionClosedEvent
import com.piotr.marketbroker.domain.subscription.event.SubscriptionEvent
import com.piotr.marketbroker.domain.subscription.Subscription
import com.piotr.marketbroker.common.logger
import com.piotr.marketbroker.domain.subscription.port.KafkaConnectPort
import com.piotr.marketbroker.domain.subscription.port.SubscriptionRepository
import com.piotr.marketbroker.domain.tick.port.TickState
import org.springframework.context.event.EventListener
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Service

@Service
class SubscriptionEventHandler (
    private val subscriptionRepository: SubscriptionRepository,
    private val tickState: TickState,
    private val kafkaConnectPort: KafkaConnectPort
) {
    private val log by logger()

    @Async
    @EventListener
    fun handleSubscriptionEvent(event: SubscriptionEvent) {
        if (!event.status) {
            log.info("Subscription failed event")
            return
        }
        kafkaConnectPort.manageConnector(event.quoteId, event.action == "subscribe")
        when (event.action) {
            "subscribe" -> {
                val s = Subscription(event.quoteId, true)
                subscriptionRepository.save(s)
                log.info("Subscription added for ${event.quoteId}")
            }

            "unsubscribe" -> {
                tickState.remove(event.quoteId)
                val s = subscriptionRepository.findById(event.quoteId)
                if (s != null && s.status) {
                    s.status = false
                    subscriptionRepository.save(s)
                    log.info("Subscription removed for ${event.quoteId}")
                } else {
                    log.error("Unsubscribe error for ${event.quoteId}")
                }

            }
        }

    }

    @Async
    @EventListener
    fun removeSubscriptions(event: SessionClosedEvent) {
        subscriptionRepository.deleteAll()
    }

}
