package com.piotr.marketbroker.application.service

import com.piotr.marketbroker.application.websocket.WebsocketService
import com.piotr.marketbroker.common.unwrap
import com.piotr.marketbroker.infrastructure.persistence.marketquote.SpringDataMarketQuotesRepository
import com.piotr.marketbroker.infrastructure.persistence.subscription.SpringDataSubscriptionsRepository
import io.micrometer.observation.annotation.Observed
import mu.KotlinLogging
import org.springframework.stereotype.Service

private val log = KotlinLogging.logger(SubscriptionsService::class.toString())

@Service
class SubscriptionsService(
    private val marketQuotesRepository: SpringDataMarketQuotesRepository,
    private val subscriptionsRepository: SpringDataSubscriptionsRepository,
    private val websocketService: WebsocketService
) {

    @Observed(name = "SubscriptionsService",
        contextualName = "getSubscriptions",
        lowCardinalityKeyValues = ["type","GET"]
    )
    fun getSubscriptions(): List<String> {
        val quoteIds = subscriptionsRepository.findByStatusTrue().map { s -> s.quoteId }
        return quoteIds.map { s -> marketQuotesRepository.findByQuoteID(s)?.toString()?:s.toString() }
    }

    @Observed(name = "SubscriptionsService",
        contextualName = "postSubscriptions",
        lowCardinalityKeyValues = ["type","POST"]
    )
    fun postSubscriptions(quoteId: Int, status: Boolean): Boolean {
        log.info("postSubscriptions request: $quoteId $status")
        val subscription = subscriptionsRepository.findById(quoteId).unwrap()
        return if ((subscription==null && status) || (subscription!=null && subscription.status!=status)) {
            websocketService.subscribe(quoteId, status)
            true
        } else {
            log.info("postSubscriptions: $quoteId already $status or cannot unsubscribe when not subscribed")
            false
        }
    }

}
