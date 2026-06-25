package com.piotr.marketbroker.application.handler

import ai.symmetrical.kafka.producer.VariableTopicMessageProducer
import com.piotr.marketbroker.application.event.OpeningOrderExecutedEvent
import com.piotr.marketbroker.application.event.kafka.transaction.TransactionEvent
import com.piotr.marketbroker.application.event.kafka.transaction.TransactionType
import com.piotr.marketbroker.common.logger
import com.piotr.marketbroker.configuration.kafka.KafkaTopics.TOPIC_TRANSACTIONS
import com.piotr.marketbroker.domain.order.port.OrdersRepository
import org.springframework.context.event.EventListener
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Service
import java.time.LocalDateTime
import java.time.ZoneOffset

@Service
class OpeningOrderExecutedEventHandler(
    private val ordersRepository: OrdersRepository,
    private val producer: VariableTopicMessageProducer<TransactionEvent>,
) {
    private val log by logger()

    @Async
    @EventListener
    fun handleOpeningOrderExecutedEvent(event: OpeningOrderExecutedEvent) {

        val orders = ordersRepository.findOrdersByOrderIdIn(event.executedOpeningOrderIds)

        orders.forEach { order ->

            if (order.positionId>0) {
                log.error("Order to be filled (id=${order.orderId}) is already filled, positionId=${order.positionId}")
                return@forEach
            }

            val matchedPositions = event.newPositions.filter { position ->
                        order.marketId == position.marketId                    //TODO: add check for stopLoss and takeProfit
                        && order.quoteId == position.quoteId
                        && order.stake == position.stake
                        && when (position.direction.lowercase()) {
                    "buy" -> position.openingPriceDecimal <= order.price && order.direction == 1
                    "sell" -> position.openingPriceDecimal >= order.price && order.direction == -1
                    else -> {
                        error("Unknown position direction")
                    }
                }
            }

            if (matchedPositions.size == 1) {
//                matchedOrder[0].orderId = position.orderId                    // ignore change of orderId upon order execution, this is to maintain lineage of order from submission till close
                order.positionId = matchedPositions[0].positionId
                order.open_price = matchedPositions[0].openingPriceDecimal
                order.open_date = matchedPositions[0].creationTimeUTC
                order.updatedAt = LocalDateTime.now()
                ordersRepository.save(order)

                producer.produce(
                    TransactionEvent(
                        o = order.orderId,
                        p = order.positionId,
                        type = TransactionType.FILLED,
                        price = order.open_price,
                        sl = order.stopOrderPrice,
                        tp = order.limitOrderPrice,
                        t = order.open_date?.toEpochSecond(ZoneOffset.UTC)?:0L
                    ),
                    TOPIC_TRANSACTIONS, null)

                log.info("Matched opening order ${order.orderId} with position opened at ${matchedPositions[0].creationTimeUTC}, positionId: ${matchedPositions[0].positionId}")
            } else {
                log.warn("Opening order ${order.orderId} matched with ${matchedPositions.size} positions")
            }

        }
        
    }
}
