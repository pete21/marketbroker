package com.piotr.marketbroker.application.service

import com.fasterxml.jackson.annotation.JsonAutoDetect
import com.fasterxml.jackson.annotation.PropertyAccessor
import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.piotr.marketbroker.infrastructure.http.HttpAdapter
import com.piotr.marketbroker.infrastructure.persistence.marketgroup.MarketGroup
import com.piotr.marketbroker.infrastructure.persistence.marketgroup.SpringDataMarketGroupRepository
import com.piotr.marketbroker.infrastructure.persistence.marketquote.MarketQuote
import com.piotr.marketbroker.infrastructure.persistence.marketquote.SpringDataMarketQuotesRepository
import org.springframework.stereotype.Service

private const val GET_MARKET_GROUP = "{\"superGroupId\":%d}"
private const val GROUP_QUOTES_QUERY =
    "{\"groupID\":%d,\"keyword\":\"\",\"portfolio\":false,\"search\":false,\"popular\":false}"

@Service
class InstrumentsService(
    private val springDataMarketGroupRepository: SpringDataMarketGroupRepository,
    private val springDataMarketQuotesRepository: SpringDataMarketQuotesRepository,
    private val httpAdapter: HttpAdapter
) {

    private val mapper = jacksonObjectMapper()
        .setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY)
        .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)

    fun getInstrumentGroups(): List<String> {
        val marketGroups = springDataMarketGroupRepository.findAll()
        return marketGroups.map { m: MarketGroup -> m.toString() }
    }

    fun getInstrumentQuotes(): List<String> {
        val marketQuotes = springDataMarketQuotesRepository.findAll()
        return marketQuotes.map { m: MarketQuote -> m.toString() }
    }

    fun postInstrumentGroups() {
        val response = httpAdapter.postRequest("GetMarketSuperGroup", "", null);
        saveMarketGroups(response.body.substring(5, response.body.length - 1))
        val groupIDs = springDataMarketGroupRepository.findAll()
            .filter { m -> !m.isWhiteLabelPopularMarket }
            .map { m -> m.id }
        groupIDs.forEach { g-> httpClientGetMarketGroup(g!!) }
    }

    private fun httpClientGetMarketGroup(groupId: Int) {
        val query = String.format(GET_MARKET_GROUP, groupId);
        val response = httpAdapter.postRequest("GetMarketGroup", query, null);
        saveMarketGroups(response.body.substring(5, response.body.length-1));
    }

    private fun saveMarketGroups(data: String) {
        try {
            val marketGroups: ArrayList<MarketGroup> = mapper.readValue(data)
            springDataMarketGroupRepository.saveAll(marketGroups)
            log.info("Market groups saved: {}", marketGroups.map { m->m.name })
        } catch (e: JsonProcessingException) {
            log.error("saveMarketGroups error");
            // TODO Auto-generated catch block
        }
    }

    fun postInstrumentQuotes() {
        val marketGroups = springDataMarketGroupRepository.findAll()
        marketGroups.forEach { m -> if (m.isSuperGroup) httpClientGetMarketQuote(m.id!!) }
    }

    private fun httpClientGetMarketQuote(groupId: Int) {
        val query = String.format(GROUP_QUOTES_QUERY, groupId)
        val response = httpAdapter.postRequest("GetMarketQuote", query, null)

        try {
            val quotesList: List<MarketQuote> =
                mapper.readValue(response.body.substring(5, response.body.length-1))
            springDataMarketQuotesRepository.saveAll(quotesList)
            log.info("Market quotes saved: {}", quotesList.map { m -> m.marketName })
        } catch (e: IllegalArgumentException) {
            log.error("httpClientGetMarketQuote error")
            // TODO Auto-generated catch block
        }
    }

}