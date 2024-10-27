package com.piotr.marketbroker.infrastructure.persistence.marketquote

import com.piotr.marketbroker.domain.marketquote.MarketQuote
import com.piotr.marketbroker.domain.marketquote.port.MarketQuoteRepository
import org.springframework.stereotype.Repository

@Repository
class JPAMarketQuotesRepository(
    private val springDataMarketQuotesRepository: SpringDataMarketQuotesRepository
) : MarketQuoteRepository {
    override fun findAll(): List<MarketQuote> {
        return springDataMarketQuotesRepository.findAll().toList()
    }

    override fun saveAll(quotesList: List<MarketQuote>): List<MarketQuote> {
        return springDataMarketQuotesRepository.saveAll(quotesList).toList()
    }

    override fun findByQuoteID(s: Int): MarketQuote? {
        return springDataMarketQuotesRepository.findByQuoteID(s)
    }
}