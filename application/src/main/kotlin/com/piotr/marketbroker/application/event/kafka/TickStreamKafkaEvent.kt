package com.piotr.marketbroker.application.event.kafka

import ai.symmetrical.kafka.common.KafkaMessage
import ai.symmetrical.kafka.common.SendToTopic
import com.piotr.marketbroker.configuration.kafka.KafkaTopics
import com.piotr.marketbroker.infrastructure.persistence.tick.Tick

@KafkaMessage(type = "TICKSTREAMKAFKAEVENT", sourceSystem = "TICKSTREAM_PRODUCER")
@SendToTopic(topicSuffix = KafkaTopics.TOPIC_TICKSTREAM)
class TickStreamKafkaEvent(
    val q: Int,
    val b: Float,
    val a: Float,
    val m: Float,
    val t: Long
) {
    companion object {
        fun invoke(tick: Tick): TickStreamKafkaEvent {
            return TickStreamKafkaEvent(tick.quoteId, tick.bid, tick.ask, tick.mid, tick.time*1000L + tick.millis)
        }
    }
}
