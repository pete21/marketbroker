package com.piotr.marketbroker.infrastructure.persistence.marketquote

import org.springframework.data.repository.CrudRepository

interface SpringDataMarketQuotesRepository: CrudRepository<MarketQuote, Int>