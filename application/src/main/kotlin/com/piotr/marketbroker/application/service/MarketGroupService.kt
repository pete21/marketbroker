package com.piotr.marketbroker.application.service

import com.fasterxml.jackson.annotation.JsonAutoDetect
import com.fasterxml.jackson.annotation.PropertyAccessor
import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.piotr.marketbroker.common.logger
import com.piotr.marketbroker.domain.marketgroup.MarketGroup
import com.piotr.marketbroker.domain.marketgroup.port.MarketGroupRepository
import com.piotr.marketbroker.infrastructure.http.ApacheHttpAdapter
import com.piotr.marketbroker.infrastructure.http.RequestHeaders
import org.springframework.stereotype.Service

const val GET_MARKET_GROUP = "{\"superGroupId\":%d}"

@Service
class MarketGroupService(
    private val marketGroupRepository: MarketGroupRepository,
    private val httpAdapter: ApacheHttpAdapter
) {

    private val log by logger()

    private val mapper = jacksonObjectMapper()
        .setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY)
        .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)

    fun getInstrumentGroups(): List<String> {
        val marketGroups = marketGroupRepository.findAll()
        return marketGroups.map { m: MarketGroup -> m.toString() }
    }

    fun postInstrumentGroups() {
        val response = httpAdapter.postRequest("GetMarketSuperGroup", "{}", RequestHeaders.postHeaders)
        saveMarketGroups(response.body.substring(5, response.body.length - 1))
        val groupIDs = marketGroupRepository.findAll()
            .filter { m -> !m.isWhiteLabelPopularMarket }
            .map { m -> m.groupId }
        groupIDs.forEach { g-> httpClientGetMarketGroup(g) }
    }

    private fun httpClientGetMarketGroup(groupId: Int) {
        val query = String.format(GET_MARKET_GROUP, groupId)
        val response = httpAdapter.postRequest("GetMarketGroup", query, RequestHeaders.postHeaders)
        saveMarketGroups(response.body.substring(5, response.body.length-1))
    }

    private fun saveMarketGroups(data: String) {
        try {
            val marketGroups: ArrayList<MarketGroup> = mapper.readValue(data)
            marketGroupRepository.saveAll(marketGroups)
            log.info("Market groups saved: {}", marketGroups.map { m->m.name })
        } catch (e: JsonProcessingException) {
            log.error("saveMarketGroups error")
            // TODO Auto-generated catch block
        }
    }

}
