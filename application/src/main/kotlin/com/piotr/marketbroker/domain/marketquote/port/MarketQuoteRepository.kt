package com.piotr.marketbroker.domain.marketquote.port

import com.piotr.marketbroker.domain.marketquote.MarketQuote

interface MarketQuoteRepository {

    fun findAll(): List<MarketQuote>

    fun saveAll(quotesList: List<MarketQuote>) : List<MarketQuote>

    fun findByQuoteID(s: Int) : MarketQuote?

}
