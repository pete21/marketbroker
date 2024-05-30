package com.piotr.marketbroker.infrastructure.questdb

import com.piotr.marketbroker.infrastructure.http.ApacheHttpAdapter
import com.piotr.marketbroker.infrastructure.http.RequestHeaders
import io.micrometer.observation.annotation.Observed
import mu.KotlinLogging
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

private const val CREATE_QUESTDB_TABLE_QUERY = "CREATE TABLE IF NOT EXISTS %s(timestamp TIMESTAMP,q symbol CAPACITY 2,b float,a float) TIMESTAMP(timestamp) PARTITION BY DAY WAL DEDUP UPSERT KEYS(timestamp)"
private const val QUESTDB_TABLE_NAME_TEMPLATE = "TICKSTREAM_%d"

private val log = KotlinLogging.logger(QuestDbAdapter::class.toString())

@Service
class QuestDbAdapter(
    private val httpAdapter: ApacheHttpAdapter
) {
    @Value("\${com.piotr.questdb.url}")
    lateinit var questdbUrl: String

    @Observed(name = "QuestDbAdapter",
        contextualName = "createTable",
        lowCardinalityKeyValues = ["type","questdb"]
    )
    fun createTable(quoteId: Int): Boolean {
        val name = String.format(QUESTDB_TABLE_NAME_TEMPLATE, quoteId)
        log.info("Creating QuestDB table: $name")
        val query = URLEncoder.encode(String.format(CREATE_QUESTDB_TABLE_QUERY, name), StandardCharsets.UTF_8.toString())
        val response = httpAdapter.getRequest("$questdbUrl/exec?query=${query}", RequestHeaders.jsonRequestHeaders)
        return response.statusCode==200
    }
}
