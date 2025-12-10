package com.piotr.marketbroker.application.event.kafka.transaction

import ai.symmetrical.kafka.common.KafkaMessage
import ai.symmetrical.kafka.common.SendToTopic
import com.piotr.marketbroker.configuration.kafka.KafkaTopics

@KafkaMessage(type = "transaction-event", sourceSystem = "marketbroker")
@SendToTopic(topicSuffix = KafkaTopics.TOPIC_TRANSACTIONS)
data class TransactionEvent(
    val o: Int,
    val type: TransactionType,
    val price: Float,
    val sl: Float? = null,
    val tp: Float? = null,
    val t: Long
)
//{
//    companion object {
//        fun new(
//            q: Int,
//            type: TransactionType,
//            price: Float? = null,
//            t: Long
//        ): TransactionEvent {
//            return TransactionEvent(q, type, price, t)
//        }
//    }
//}

enum class TransactionType {
    FILLED, CLOSED, REJECTED, CANCELLED
}
