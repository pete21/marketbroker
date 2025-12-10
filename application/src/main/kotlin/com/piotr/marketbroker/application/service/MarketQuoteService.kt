package com.piotr.marketbroker.application.service

import com.fasterxml.jackson.annotation.JsonAutoDetect
import com.fasterxml.jackson.annotation.PropertyAccessor
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.piotr.marketbroker.common.logger
import com.piotr.marketbroker.domain.marketgroup.MarketGroup
import com.piotr.marketbroker.domain.marketgroup.port.MarketGroupRepository
import com.piotr.marketbroker.domain.marketquote.MarketQuote
import com.piotr.marketbroker.domain.marketquote.port.MarketQuoteRepository
import com.piotr.marketbroker.infrastructure.http.ApacheHttpAdapter
import com.piotr.marketbroker.infrastructure.http.RequestHeaders
import org.springframework.stereotype.Service

const val GROUP_QUOTES_QUERY =
    "{\"groupID\":%d,\"keyword\":\"\",\"portfolio\":false,\"search\":false,\"popular\":false}"

@Service
class MarketQuoteService(
    private val marketQuoteRepository : MarketQuoteRepository,
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

    fun getInstrumentQuotes(): List<String> {
        val marketQuotes = marketQuoteRepository.findAll()
        return marketQuotes.map { m: MarketQuote -> m.toString() }
    }

    fun postInstrumentQuotes() {
        val marketGroups = marketGroupRepository.findAll()
        if (marketGroups.isEmpty()) {
            log.error("Market groups not found")
            return
        }
        marketQuoteRepository.deleteAll()
        marketGroups.forEach { m -> if (!m.isSuperGroup) httpClientGetMarketQuote(m.groupId) }
    }

    private fun httpClientGetMarketQuote(groupId: Int) {
        val query = String.format(GROUP_QUOTES_QUERY, groupId)
        val response = httpAdapter.postRequest("GetMarketQuote", query, RequestHeaders.postHeaders)

        try {
            val quotesList: List<MarketQuote> =
                mapper.readValue(response.body.substring(5, response.body.length-1))
            //TODO: save groupId with every MarketQuote, requires additional groupId attribute
            marketQuoteRepository.saveAll(quotesList)
            log.info("Market quotes saved: {}", quotesList.map { m -> m.marketName })
        } catch (e: IllegalArgumentException) {
            log.error("httpClientGetMarketQuote error")
            // TODO Auto-generated catch block
        }
    }

}
