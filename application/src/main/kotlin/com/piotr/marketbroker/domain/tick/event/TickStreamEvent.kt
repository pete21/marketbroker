package com.piotr.marketbroker.domain.tick.event

import ai.symmetrical.kafka.common.KafkaMessage
import ai.symmetrical.kafka.common.SendToTopic
import com.piotr.marketbroker.configuration.kafka.KafkaTopics
import com.piotr.marketbroker.domain.tick.Tick

@KafkaMessage(type = "tick-stream", sourceSystem = "marketbroker")
@SendToTopic(topicSuffix = KafkaTopics.TOPIC_TICKSTREAM)
data class TickStreamEvent(
    val q: Int,
    val b: Float,
    val a: Float,
//    val m: Float,
    val t: Long
) {
    companion object {
        fun invoke(tick: Tick): TickStreamEvent {
            return TickStreamEvent(tick.quoteId, tick.bid, tick.ask, tick.time*1000L + tick.millis)
        }
    }
}
