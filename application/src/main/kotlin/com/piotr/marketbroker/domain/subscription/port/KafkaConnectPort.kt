package com.piotr.marketbroker.domain.subscription.port

import com.piotr.marketbroker.application.event.SessionClosedEvent

interface KafkaConnectPort {

    fun manageConnector(quoteId: Int, status: Boolean)

    fun deleteAtSessionClosed(event: SessionClosedEvent)
}
