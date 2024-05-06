package com.piotr.marketbroker.application.event.handler

import ai.symmetrical.kafka.producer.VariableTopicMessageProducer
import com.piotr.marketbroker.application.event.TickEvent
import com.piotr.marketbroker.application.event.kafka.TickStreamKafkaEvent
import com.piotr.marketbroker.application.mapper.TickMapper
import com.piotr.marketbroker.configuration.kafka.KafkaTopics.TOPIC_TICKSTREAM_TICKER_TEMPLATE
import com.piotr.marketbroker.infrastructure.persistence.keys.KeysRepository
import com.piotr.marketbroker.infrastructure.persistence.tick.SpringDataTickRepository
import org.springframework.context.event.EventListener
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Service


@Service
class TickEventHandler (
    private val producer: VariableTopicMessageProducer<TickStreamKafkaEvent>,
    private val tickRepository: SpringDataTickRepository,
    private val keysRepository: KeysRepository
)
{
    @Async
    @EventListener
    fun handleTickEvent(event: TickEvent) {
        val ticks = TickMapper.toTick(event.ticks)
        ticks.forEach { producer.produce(TickStreamKafkaEvent.invoke(it),
            String.format(TOPIC_TICKSTREAM_TICKER_TEMPLATE, it.quoteId), null) }

        tickRepository.saveAll(ticks)

        ticks.groupBy { it.quoteId }
            .map { Pair(it.key, it.value.maxBy { v -> v.longtime }) }
            .forEach { (k, v) -> keysRepository.put(k, v) }
    }
}
