package com.piotr.marketbroker.application.service

import com.piotr.marketbroker.application.websocket.WebsocketService
import com.piotr.marketbroker.infrastructure.persistence.marketquote.SpringDataMarketQuotesRepository
import com.piotr.marketbroker.infrastructure.persistence.subscription.SpringDataSubscriptionsRepository
import org.springframework.stereotype.Service

@Service
class SubscriptionsService(
    private val marketQuotesRepository: SpringDataMarketQuotesRepository,
    private val subscriptionsRepository: SpringDataSubscriptionsRepository,
    private val websocketService: WebsocketService
) {

    fun getSubscriptions(): List<String> {
        val subscribed = subscriptionsRepository.findAll().filter { s -> s.status }.map { s -> s.id }
        return marketQuotesRepository.findAllById(subscribed).map { s -> s.toString() }
    }


    fun postSubscriptions(quoteId: Int, status: Boolean): Boolean {
        log.info("postSubscriptions request: {} {}", quoteId, status)
        return websocketService.subscribe(quoteId, status)
    }

}