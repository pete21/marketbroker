package com.piotr.marketbroker.application.event.handler

import ai.symmetrical.kafka.producer.FixedTopicMessageProducer
import com.piotr.marketbroker.application.event.TickEvent
import com.piotr.marketbroker.application.event.TickStreamKafkaEvent
import com.piotr.marketbroker.application.mapper.TickMapper
import com.piotr.marketbroker.infrastructure.persistence.keys.KeysRepository
import com.piotr.marketbroker.infrastructure.persistence.tick.SpringDataTickRepository
import org.springframework.context.event.EventListener
import java.util.*

class TickEventHandler (
    private val producer: FixedTopicMessageProducer<TickStreamKafkaEvent>,
    private val tickRepository: SpringDataTickRepository,
    private val keysRepository: KeysRepository
)
{
    @EventListener
    fun HandleTickEvent(event: TickEvent) {
        val tickdata = event.ticks
        val ticks = TickMapper.toTick(tickdata)
        producer.produce(TickStreamKafkaEvent.invoke(ticks), null, null)

        tickRepository.saveAll(ticks)

        ticks.groupBy { it.quoteId }
            .map { Pair(it.key, it.value.maxBy { v -> v.longtime }) }
            .forEach { (k, v) -> keysRepository.put(k, v) }
    }
}
