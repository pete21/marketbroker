package com.piotr.marketbroker.application.handler

import ai.symmetrical.kafka.producer.VariableTopicMessageProducer
import com.piotr.marketbroker.application.service.OrdersService
import com.piotr.marketbroker.application.event.PositionClosedEvent
import com.piotr.marketbroker.application.event.kafka.transaction.TransactionEvent
import com.piotr.marketbroker.application.event.kafka.transaction.TransactionType
import com.piotr.marketbroker.common.logger
import com.piotr.marketbroker.configuration.kafka.KafkaTopics.TOPIC_TRANSACTIONS
import com.piotr.marketbroker.domain.order.port.OrdersRepository
import org.springframework.context.event.EventListener
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Service
import java.time.LocalDateTime
import java.time.OffsetDateTime
import java.time.ZoneOffset

@Service
class PositionClosedEventHandler(
    private val ordersRepository: OrdersRepository,
    private val ordersService: OrdersService,
    private val producer: VariableTopicMessageProducer<TransactionEvent>,
    ) {
    private val log by logger()

    var lastGetHistory = OffsetDateTime.now(ZoneOffset.UTC).toLocalDateTime().minusDays(1)

    @Async
    @EventListener
    fun handlePositionClosedEvent(event: PositionClosedEvent) {
        val transactionHistory = ordersService.getHistory()
        val filteredTransactionHistory = transactionHistory?.filter { it.TransactionDate>lastGetHistory }
        if (filteredTransactionHistory==null) {
            log.error("No history orders since last check at ${lastGetHistory}")
        }
        lastGetHistory = OffsetDateTime.now(ZoneOffset.UTC).toLocalDateTime().minusSeconds(3)

        val orders = ordersRepository.findOrdersByPositionIdIn(event.positions.map { it.positionId})          //orderId changes with execution/close

        orders.forEach { order ->
            if (!order.active) {
                log.error("Order to be closed (id=${order.orderId}) is already closed")
                return@forEach
            }

            val matchedHistoryOrders = filteredTransactionHistory?.filter { historyOrder ->
                val timeFrom = historyOrder.OpenPeriod.minusSeconds(3)
                val timeTo = historyOrder.OpenPeriod.plusSeconds(3)
                order.open_date!! >= timeFrom && order.open_date!! <= timeTo
                        && order.open_price == historyOrder.OpenPrice
                        && order.direction * order.stake == historyOrder.Amount
            } ?: emptyList()
            if (matchedHistoryOrders.size == 1) {
                order.active = false
                order.close_price = matchedHistoryOrders[0].ClosePrice
                order.updatedAt = LocalDateTime.now()
                order.close_date = matchedHistoryOrders[0].TransactionDate
                ordersRepository.save(order)

                producer.produce(
                    TransactionEvent(
                        o = order.orderId,
                        type = TransactionType.CLOSED,
                        price = order.close_price,
                        t = order.close_date?.toEpochSecond(ZoneOffset.UTC)?:0L
                    ),
                    TOPIC_TRANSACTIONS, null)

                log.info("Matched order ${order.orderId} with history order closed at ${matchedHistoryOrders[0].TransactionDate}")
                return@forEach
            } else {
                log.error("Order ${order.orderId} matched with ${matchedHistoryOrders.size} history orders, expected 1")
            }

        }

    }
}
