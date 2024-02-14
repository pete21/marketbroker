package com.piotr.marketbroker.application.event.kafka

import ai.symmetrical.kafka.common.KafkaMessage
import ai.symmetrical.kafka.common.SendToTopic
import com.piotr.marketbroker.configuration.kafka.KafkaTopics
import com.piotr.marketbroker.infrastructure.persistence.tick.Tick

@KafkaMessage(type = "TICKSTREAMKAFKAEVENT", sourceSystem = "TICKSTREAM_PRODUCER")
@SendToTopic(topicSuffix = KafkaTopics.TOPIC_TICKSTREAM)
data class TickStreamKafkaEvent(val p: List<PriceTick>) {
    companion object {
        fun invoke(t: List<Tick>): TickStreamKafkaEvent {
            return TickStreamKafkaEvent(t.map { tick: Tick ->
                PriceTick(tick.quoteId, tick.bid, tick.ask, tick.mid, tick.time, tick.millis)
            } )
        }
    }
}
class PriceTick (
    val q: Int,
    val b: Float,
    val a: Float,
    val m: Float,
    val t: Int,
    val mi: Int,
)