package com.piotr.marketbroker.application.handler

import ai.symmetrical.kafka.producer.VariableTopicMessageProducer
import com.piotr.marketbroker.application.event.MarketOrderExecutedEvent
import com.piotr.marketbroker.application.event.kafka.transaction.TransactionEvent
import com.piotr.marketbroker.application.event.kafka.transaction.TransactionType
import com.piotr.marketbroker.common.logger
import com.piotr.marketbroker.configuration.kafka.KafkaTopics.TOPIC_TRANSACTIONS
import com.piotr.marketbroker.domain.order.port.OrdersRepository
import org.springframework.context.event.EventListener
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Service
import java.time.ZoneOffset

@Service
class MarketOrderExecutedEventHandler(
    private val ordersRepository: OrdersRepository,
    private val producer: VariableTopicMessageProducer<TransactionEvent>,
) {
    private val log by logger()

    @Async
    @EventListener
    fun handleMarketOrderExecutedEvent(event: MarketOrderExecutedEvent) {
        Thread.sleep(2000)
        val orders = ordersRepository.findOrdersByActiveTrue().filter { it.orderModeID == 0 }

        log.info("Found ${orders.size} active market orders. ${event.newMarketPositions.size} new positions to match.")
        orders.forEach { order ->

            val matchedPositions = event.newMarketPositions.filter { position ->
                        order.orderId == position.orderId
            }

            if (matchedPositions.size == 1) {

                producer.produce(
                    TransactionEvent(
                        o = order.orderId,
                        p = matchedPositions[0].positionId,
                        type = TransactionType.FILLED,
                        price = order.open_price,
                        sl = order.stopOrderPrice,
                        tp = order.limitOrderPrice,
                        t = order.open_date?.toEpochSecond(ZoneOffset.UTC)?:0L
                    ),
                    TOPIC_TRANSACTIONS, null)

                log.info("Matched market order ${order.orderId} with position opened at ${matchedPositions[0].creationTimeUTC}, positionId: ${matchedPositions[0].positionId}")
            } else {
                log.warn("Market order ${order.orderId} matched with ${matchedPositions.size} positions")
            }

        }
        
    }
}
