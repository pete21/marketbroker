package com.piotr.marketbroker.configuration.kafka

import kotlin.reflect.full.declaredMemberProperties

object KafkaTopics {

    const val TOPIC_TEST_TOPIC = "TEST_TOPIC"
    const val TOPIC_ORDERS_TOPIC = "ORDERS_TOPIC"
    const val TOPIC_MARKET_TICKS = "MARKET_TICKS_TOPIC"
    const val TOPIC_ACCOUNT_DETAILS = "ACCOUNT_DETAILS_TOPIC"
    const val TOPIC_JSON_DATA = "JSON_DATA_TOPIC"
    const val TOPIC_TEXT_DATA = "TEXT_DATA_TOPIC"

    fun getAllTopics(): List<String> {
        val fields = KafkaTopics::class.declaredMemberProperties
        return fields.map { it.call() as String }
    }
}
