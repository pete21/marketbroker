package com.piotr.marketbroker.application.event.kafka

import ai.symmetrical.kafka.common.KafkaMessage
import ai.symmetrical.kafka.common.SendToTopic
import com.piotr.marketbroker.configuration.kafka.KafkaTopics

@KafkaMessage(type = "JSONDATAKAFKAEVENT", sourceSystem = "JSONDATA_PRODUCER")
@SendToTopic(topicSuffix = KafkaTopics.TOPIC_JSON_DATA)
class JsonDataKafkaEvent (
    val type: String,
    val data: String
)