package com.piotr.marketbroker.application.service

import com.piotr.marketbroker.application.websocket.WebsocketService
import com.piotr.marketbroker.common.logger
import com.piotr.marketbroker.domain.marketquote.port.MarketQuoteRepository
import com.piotr.marketbroker.domain.subscription.port.SubscriptionRepository
import org.springframework.stereotype.Service

@Service
class SubscriptionsService(
    private val marketQuotesRepository: MarketQuoteRepository,
    private val subscriptionsRepository: SubscriptionRepository,
    private val websocketService: WebsocketService
) {

    private val log by logger()

    fun getSubscriptions(): List<String> {
        val quoteIds = subscriptionsRepository.findByStatusTrue().map { s -> s.quoteId }
        return quoteIds.map { s -> marketQuotesRepository.findByQuoteID(s)?.toString()?:s.toString() }
    }

    fun postSubscriptions(quoteId: Int, status: Boolean): Boolean {
        log.info("postSubscriptions request: $quoteId $status")
        val subscription = subscriptionsRepository.findById(quoteId)
        return if ((subscription==null && status) || (subscription!=null && subscription.status!=status)) {
            websocketService.subscribe(quoteId, status)
            true
        } else {
            log.info("postSubscriptions: $quoteId already $status or cannot unsubscribe when not subscribed")
            false
        }
    }

    fun renewSubscriptions() {
        log.info("renewSubscriptions request")
        val subscriptions = subscriptionsRepository.findByStatusTrue()
        subscriptions.forEach { websocketService.subscribe(it.quoteId, it.status) }
    }

}
