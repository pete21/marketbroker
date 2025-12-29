package com.piotr.marketbroker.infrastructure.kafkaconnect

import com.piotr.marketbroker.application.event.SessionClosedEvent
import com.piotr.marketbroker.configuration.kafka.KafkaTopics.TOPIC_TICKSTREAM_TICKER_TEMPLATE
import com.piotr.marketbroker.infrastructure.http.ApacheHttpAdapter
import com.piotr.marketbroker.infrastructure.http.RequestHeaders
import com.piotr.marketbroker.infrastructure.questdb.QuestDbAdapter
import com.piotr.marketbroker.common.logger
import com.piotr.marketbroker.configuration.kafkaconnect.KafkaConnectConfigurationProperties
import com.piotr.marketbroker.domain.subscription.port.KafkaConnectPort
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.event.EventListener
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Component


private const val CREATE_CONNECTOR_DTO = "{\"name\":\"%s\",\"config\":{\"topics\":\"%s\",\"table\":\"%s\",\"connector.class\":\"io.questdb.kafka.QuestDBSinkConnector\",\"tasks.max\":\"1\",\"key.converter\":\"org.apache.kafka.connect.storage.StringConverter\",\"value.converter\":\"org.apache.kafka.connect.json.JsonConverter\",\"value.converter.schemas.enable\":\"false\",\"client.conf.string\":\"http::addr=%s;\",\"allowed.lag\":100,\"include.key\":false,\"symbols\":\"q\",\"doubles\":\"b,a\",\"timestamp.field.name\":\"t\",\"timestamp.units\":\"millis\"}}"
private const val CREATE_CONNECTOR_DTO_NO_TYPES = "{\"name\":\"%s\",\"config\":{\"topics\":\"%s\",\"table\":\"%s\",\"connector.class\":\"io.questdb.kafka.QuestDBSinkConnector\",\"tasks.max\":\"1\",\"key.converter\":\"org.apache.kafka.connect.storage.StringConverter\",\"value.converter\":\"org.apache.kafka.connect.json.JsonConverter\",\"value.converter.schemas.enable\":\"false\",\"client.conf.string\":\"http::addr=%s;\",\"allowed.lag\":100,\"include.key\":false,\"symbols\":\"q\",\"timestamp.field.name\":\"t\",\"timestamp.units\":\"millis\"}}"

private const val CONNECTOR_NAME_TEMPLATE = "TICKSTREAM_%d"
private const val TOPIC_NAME = "%s.%s.%s"


@Component
class KafkaConnectAdapter(
    private val httpAdapter: ApacheHttpAdapter,
    private val questDbAdapter: QuestDbAdapter,
    private val kafkaConnectConfigurationProperties: KafkaConnectConfigurationProperties
) : KafkaConnectPort {

    private val log by logger()

    @Value("\${spring.application.name}")
    lateinit var appName: String
    @Value("\${ai.symmetrical.kafka.env}")
    lateinit var env: String


    private val connectors = mutableMapOf<Int, ConnectorStatus>()

    override fun manageConnector(quoteId: Int, status: Boolean) {
        val connectorStatus = connectors[quoteId]
        if (status) {
            when (connectorStatus) {
                ConnectorStatus.UNASSIGNED -> {}
                ConnectorStatus.STARTING -> {}
                ConnectorStatus.RUNNING -> {}
                ConnectorStatus.PAUSED, ConnectorStatus.STOPPED -> resumeConnector(quoteId)
                ConnectorStatus.FAILED -> restartConnector(quoteId)
                null -> {
                    questDbAdapter.createTable(quoteId)
                    createConnector(quoteId)
                }
            }
        } else {
            when (connectorStatus) {
                ConnectorStatus.STARTING, ConnectorStatus.RUNNING,
                ConnectorStatus.FAILED, ConnectorStatus.UNASSIGNED -> pauseConnector(quoteId)
                ConnectorStatus.PAUSED, ConnectorStatus.STOPPED,
                null -> {}
            }
        }
    }

    fun deleteConnectors() {
        connectors.keys.forEach { deleteConnector(it) }
        connectors.clear()
    }

    private fun connectorStatus(quoteId: Int): ConnectorStatus? {
        val name = String.format(CONNECTOR_NAME_TEMPLATE, quoteId)
        val response = httpAdapter.getRequest("${kafkaConnectConfigurationProperties.url}/connectors/$name/status", RequestHeaders.jsonRequestHeaders)
        return null
    }

    private fun listConnectors(): List<String>? {
        val response = httpAdapter.getRequest("${kafkaConnectConfigurationProperties.url}/connectors", RequestHeaders.jsonRequestHeaders)
        return null
    }


    private fun createConnector(quoteId: Int): Boolean {
        val name = String.format(CONNECTOR_NAME_TEMPLATE, quoteId)
        val topicName = String.format(
            TOPIC_NAME, appName, env,
            String.format(TOPIC_TICKSTREAM_TICKER_TEMPLATE, quoteId)
        ).uppercase()
        log.info("Creating QuestDBSinkConnector: $name, Topic: $topicName")
        val response = httpAdapter.postRequest(
            "${kafkaConnectConfigurationProperties.url}/connectors",
            String.format(
                CREATE_CONNECTOR_DTO_NO_TYPES,
                name,
                topicName,
                name,
                kafkaConnectConfigurationProperties.questdbHost
            ),
            RequestHeaders.jsonRequestHeaders
        )
        if (response.statusCode / 100 == 2 || response.statusCode == 409) {
            connectors[quoteId] = ConnectorStatus.RUNNING
            return true
        }
        log.error("Error creating QuestDBSinkConnector: $name, Status: ${response.statusCode}, Body: ${response.body}")
        return false
    }

    private fun deleteConnector(quoteId: Int): Boolean {
        val name = String.format(CONNECTOR_NAME_TEMPLATE, quoteId)
        log.info("Removing QuestDBSinkConnector: $name")
        val response = httpAdapter.deleteRequest("${kafkaConnectConfigurationProperties.url}/connectors/$name",
            RequestHeaders.jsonRequestHeaders)
        if (response.statusCode / 100==2) {
            return true
        }
        log.error("Error removing QuestDBSinkConnector: $name, Status: ${response.statusCode}, Body: ${response.body}")
        return false
    }

    private fun pauseConnector(quoteId: Int): Boolean {
        val name = String.format(CONNECTOR_NAME_TEMPLATE, quoteId)
        log.info("Pausing QuestDBSinkConnector: $name")
        val response = httpAdapter.putRequest("${kafkaConnectConfigurationProperties.url}/connectors/$name/pause", "",
            RequestHeaders.jsonRequestHeaders)
        if (response.statusCode / 100 == 2) {
            connectors[quoteId] = ConnectorStatus.PAUSED
            return true
        }
        log.error("Error pausing QuestDBSinkConnector: $name, Status: ${response.statusCode}, Body: ${response.body}")
        return false
    }

    private fun resumeConnector(quoteId: Int): Boolean {
        val name = String.format(CONNECTOR_NAME_TEMPLATE, quoteId)
        log.info("Resuming QuestDBSinkConnector: $name")
        val response = httpAdapter.putRequest("${kafkaConnectConfigurationProperties.url}/connectors/$name/resume", "",
            RequestHeaders.jsonRequestHeaders)
        if (response.statusCode / 100==2) {
            connectors[quoteId] = ConnectorStatus.RUNNING
            return true
        }
        log.error("Error resuming QuestDBSinkConnector: $name, Status: ${response.statusCode}, Body: ${response.body}")
        return false
    }

    private fun restartConnector(quoteId: Int): Boolean {
        val name = String.format(CONNECTOR_NAME_TEMPLATE, quoteId)
        log.info("Restarting QuestDBSinkConnector: $name")
        val response = httpAdapter.postRequest("${kafkaConnectConfigurationProperties.url}/connectors/$name/restart", "",
            RequestHeaders.jsonRequestHeaders)
        if (response.statusCode / 100==2) {
            connectors[quoteId] = ConnectorStatus.RUNNING
            return true
        }
        log.error("Error restarting QuestDBSinkConnector: $name, Status: ${response.statusCode}, Body: ${response.body}")
        return false
    }

    private fun getConnectors(): String {
        val response = httpAdapter.getRequest(
            "${kafkaConnectConfigurationProperties.url}/connectors?expand=info",
            RequestHeaders.jsonRequestHeaders)
        return response.body
    }

    @Async
    @EventListener
    override fun deleteAtSessionClosed(event: SessionClosedEvent) {
        deleteConnectors()
    }
}

enum class ConnectorStatus {
    RUNNING,
    PAUSED,
    STOPPED,
    STARTING,
    FAILED,
    UNASSIGNED
}
/* Confluent:
UNASSIGNED: The connector/task has not yet been assigned to a worker.
RUNNING: The connector/task is running.
PAUSED: The connector/task has been administratively paused.
FAILED: The connector/task has failed (usually by raising an exception, which is reported in the status output).
 */