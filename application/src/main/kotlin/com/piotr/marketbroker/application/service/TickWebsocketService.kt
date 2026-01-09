package com.piotr.marketbroker.application.service

import com.piotr.marketbroker.domain.tick.event.TicksEvent
import com.piotr.marketbroker.infrastructure.websocket.broker.TickMessageObject
import org.springframework.context.event.EventListener
import org.springframework.messaging.simp.SimpMessagingTemplate
import org.springframework.stereotype.Service

@Service
class TickWebsocketService(
    private val template: SimpMessagingTemplate
) {

    private val quoteIdToSymbolIdMap: Map<Int, Int> = mapOf(
        6374 to 1,
        16917 to 2,
        872703 to 3,
        6647 to 4,
        5893 to 16,
    )

    @EventListener
    fun tickWebsocketSend(event: TicksEvent) {
        event.ticks.forEach {
            var s = quoteIdToSymbolIdMap[it.quoteId]
            if (s!=null) {
                template.convertAndSend(
                    "/topic/${s}",
                    TickMessageObject(s, it.bid, it.ask),
//                mutableMapOf<String, Any>("s" to it.quoteId)
                )
            } else {
                s = it.quoteId
            }
            template.convertAndSend(
                "/topic/ticks",
                TickMessageObject(s, it.bid, it.ask),
//                mutableMapOf<String, Any>("s" to it.quoteId)
            )
        }

    }

}
