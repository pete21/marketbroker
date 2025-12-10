package com.piotr.marketbroker.application.event

import com.piotr.marketbroker.domain.accountdetails.PositionsRecord

class MarketOrderExecutedEvent(val newMarketPositions: List<PositionsRecord>)
