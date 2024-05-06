package com.piotr.marketbroker.infrastructure.kafkaconnect

import com.piotr.marketbroker.application.event.SessionClosedEvent
import com.piotr.marketbroker.configuration.kafka.KafkaTopics.TOPIC_TICKSTREAM_TICKER_TEMPLATE
import com.piotr.marketbroker.infrastructure.http.ApacheHttpAdapter
import com.piotr.marketbroker.infrastructure.http.RequestHeaders
import mu.KotlinLogging
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.event.EventListener
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Service


private const val CREATE_CONNECTOR_DTO = "{\"name\":\"%s\",\"config\":{\"topics\":\"%s\",\"table\":\"%s\",\"connector.class\":\"io.questdb.kafka.QuestDBSinkConnector\",\"tasks.max\":\"1\",\"key.converter\":\"org.apache.kafka.connect.storage.StringConverter\",\"value.converter\":\"org.apache.kafka.connect.json.JsonConverter\",\"value.converter.schemas.enable\":\"false\",\"client.conf.string\":\"http::addr=%s;\",\"allowed.lag\":100,\"include.key\":false,\"symbols\":\"q\",\"doubles\":\"b,a,m\",\"timestamp.field.name\":\"t\",\"timestamp.units\":\"millis\"}}"
private const val CONNECTOR_NAME_TEMPLATE = "TICKSTREAM_%d"
private const val TOPIC_NAME = "%s.%s.%s"

private val log = KotlinLogging.logger(KafkaConnectAdapter::class.toString())

@Service
class KafkaConnectAdapter(
    private val httpAdapter: ApacheHttpAdapter
) {
    @Value("\${spring.application.name}")
    lateinit var appName: String
    @Value("\${ai.symmetrical.kafka.env}")
    lateinit var env: String
    @Value("\${com.piotr.kafka-connect.url}")
    lateinit var kafkaConnectUrl: String
    @Value("\${com.piotr.questdb.host}")
    lateinit var questdbUrl: String

    private val connectors = mutableMapOf<Int, ConnectorStatus>()

    fun manageConnector(quoteId: Int, status: Boolean) {
        val connectorStatus = connectors[quoteId]
        if (status) {
            when (connectorStatus) {
                ConnectorStatus.STARTING -> {}
                ConnectorStatus.RUNNING -> {}
                ConnectorStatus.PAUSED, ConnectorStatus.STOPPED -> resumeConnector(quoteId)
                ConnectorStatus.FAILED -> restartConnector(quoteId)
                null -> createConnector(quoteId)
            }
        } else {
            when (connectorStatus) {
                ConnectorStatus.STARTING, ConnectorStatus.RUNNING, ConnectorStatus.FAILED -> pauseConnector(quoteId)
                ConnectorStatus.PAUSED, ConnectorStatus.STOPPED -> {}
                null -> {}
            }
        }
    }

    fun deleteConnectors() {
        val keys = connectors.keys
        keys.forEach { deleteConnector(it) }
    }

    private fun createConnector(quoteId: Int): Boolean {
        val name = String.format(CONNECTOR_NAME_TEMPLATE, quoteId)
        val topicName = String.format(TOPIC_NAME, appName, env,
            String.format(TOPIC_TICKSTREAM_TICKER_TEMPLATE, quoteId)).uppercase()
        log.info("Creating QuestDBSinkConnector: $name, Topic: $topicName")
        val response = httpAdapter.postRequest(
            "$kafkaConnectUrl/connectors", String.format(CREATE_CONNECTOR_DTO, name, topicName, name, questdbUrl),
            RequestHeaders.jsonRequestHeaders)
        if (response.statusCode==200) {
            connectors[quoteId] = ConnectorStatus.RUNNING
            return true
        }
        return false
    }

    private fun deleteConnector(quoteId: Int): Boolean {
        val name = String.format(CONNECTOR_NAME_TEMPLATE, quoteId)
        log.info("Removing QuestDBSinkConnector: $name")
        val response = httpAdapter.deleteRequest("$kafkaConnectUrl/connectors/$name",
            RequestHeaders.jsonRequestHeaders)
        if (response.statusCode==200) {
            connectors.remove(quoteId)
            return true
        }
        return false
    }

    private fun pauseConnector(quoteId: Int): Boolean {
        val name = String.format(CONNECTOR_NAME_TEMPLATE, quoteId)
        log.info("Pausing QuestDBSinkConnector: $name")
        val response = httpAdapter.deleteRequest("$kafkaConnectUrl/connectors/$name/pause",
            RequestHeaders.jsonRequestHeaders)
        if (response.statusCode==200) {
            connectors[quoteId] = ConnectorStatus.PAUSED
            return true
        }
        return false
    }

    private fun resumeConnector(quoteId: Int): Boolean {
        val name = String.format(CONNECTOR_NAME_TEMPLATE, quoteId)
        log.info("Resuming QuestDBSinkConnector: $name")
        val response = httpAdapter.deleteRequest("$kafkaConnectUrl/connectors/$name/resume",
            RequestHeaders.jsonRequestHeaders)
        if (response.statusCode==200) {
            connectors[quoteId] = ConnectorStatus.RUNNING
            return true
        }
        return false
    }

    private fun restartConnector(quoteId: Int): Boolean {
        val name = String.format(CONNECTOR_NAME_TEMPLATE, quoteId)
        log.info("Restarting QuestDBSinkConnector: $name")
        val response = httpAdapter.deleteRequest("$kafkaConnectUrl/connectors/$name/restart",
            RequestHeaders.jsonRequestHeaders)
        if (response.statusCode==200) {
            connectors[quoteId] = ConnectorStatus.RUNNING
            return true
        }
        return false
    }

    private fun getConnectors(): String {
        val response = httpAdapter.getRequest(
            "$kafkaConnectUrl/connectors?expand=info",
            RequestHeaders.jsonRequestHeaders)
        return response.body
    }

    @Async
    @EventListener
    fun deleteAtSessionClosed(event: SessionClosedEvent) {
        deleteConnectors()
    }
}

enum class ConnectorStatus {
    RUNNING,
    PAUSED,
    STOPPED,
    STARTING,
    FAILED
}
