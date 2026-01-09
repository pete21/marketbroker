package com.piotr.marketbroker.domain.tick.handler

import ai.symmetrical.kafka.producer.VariableTopicMessageProducer
import com.piotr.marketbroker.configuration.kafka.KafkaTopics.TOPIC_TICKSTREAM_TICKER_TEMPLATE
import com.piotr.marketbroker.domain.tick.event.TickStreamEvent
import com.piotr.marketbroker.domain.tick.event.TicksEvent
import com.piotr.marketbroker.domain.tick.mapper.TickMapper
import com.piotr.marketbroker.domain.tick.port.TickState
import org.springframework.context.event.EventListener
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Service


@Service
class TickEventHandler (
    private val producer: VariableTopicMessageProducer<TickStreamEvent>,
//    private val tickRepository: TickRepository,
    private val tickState: TickState
)
{
    @Async
    @EventListener
    fun handleTickEvent(event: TicksEvent) {
        val ticks = TickMapper.toTick(event.ticks)
        ticks.forEach {
            producer.produce(
            TickStreamEvent.invoke(it),
            String.format(TOPIC_TICKSTREAM_TICKER_TEMPLATE, it.quoteId), null)
        }

//        tickRepository.saveAll(ticks)                 // do not save to mssql

        ticks.groupBy { it.quoteId }
            .map { Pair(it.key, it.value.maxBy { v -> v.longtime }) }
            .forEach { (k, v) -> tickState.put(k, v) }
    }
}
