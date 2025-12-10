package com.piotr.marketbroker.application.event

import com.piotr.marketbroker.domain.accountdetails.PositionsRecord

class OpeningOrderExecutedEvent(val newPositions: List<PositionsRecord>, val executedOpeningOrderIds: List<Int>)
