package com.piotr.marketbroker.application.event.kafka

import ai.symmetrical.kafka.common.KafkaMessage
import ai.symmetrical.kafka.common.SendToTopic
import com.piotr.marketbroker.configuration.kafka.KafkaTopics

@KafkaMessage(type = "TEXTDATAKAFKAEVENT", sourceSystem = "TEXTDATA_PRODUCER")
@SendToTopic(topicSuffix = KafkaTopics.TOPIC_TEXT_DATA)
class TextDataKafkaEvent (
    val message: String
)