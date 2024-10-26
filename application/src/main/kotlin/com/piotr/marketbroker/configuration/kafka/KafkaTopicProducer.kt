package com.piotr.marketbroker.configuration.kafka

import ai.symmetrical.kafka.topic.TopicFactory
import com.piotr.marketbroker.common.logger
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.context.ApplicationListener
import org.springframework.context.event.ContextRefreshedEvent
import org.springframework.kafka.core.KafkaAdmin
import org.springframework.stereotype.Component

@Component
@ConditionalOnProperty(
    prefix = "com.piotr.kafka.producer",
    name = ["enabled"],
    havingValue = "true",
    matchIfMissing = false
)
class KafkaTopicProducer(
    private val topicFactory: TopicFactory,
    private val kafkaAdmin: KafkaAdmin,
    @Value("\${com.piotr.kafka.default-dlt-replicas-number:1}")
    val defaultDltReplicasNumber: Int,
    @Value("\${com.piotr.kafka.default-dlt-partitions-number:1}")
    val defaultDltPartitionsNumber: Int,
) : ApplicationListener<ContextRefreshedEvent> {

    private val log by logger()

    override fun onApplicationEvent(event: ContextRefreshedEvent) {

        val topics = KafkaTopics.getAllTopics()

        createMainTopics(topics)
        createDltTopics(topics)
    }

    @Suppress("SpreadOperator")
    private fun createMainTopics(topics: List<String>) {
        log.info("Main topics creation process started")
        kafkaAdmin.createOrModifyTopics(*topics.map { topicFactory.createTopic(it) }.toTypedArray())
        log.info("Main topics creation process finished")
    }

    @Suppress("SpreadOperator")
    private fun createDltTopics(topics: List<String>) {
        log.info("DLT topics creation process started")
        val topicsToBeCreated = topics.map {
            topicFactory.createTopic(
                "$it.DLT",
                defaultDltPartitionsNumber,
                defaultDltReplicasNumber
            )
        }
        kafkaAdmin.createOrModifyTopics(*topicsToBeCreated.toTypedArray())
        log.info("DLT topics creation process finished")
    }
}
