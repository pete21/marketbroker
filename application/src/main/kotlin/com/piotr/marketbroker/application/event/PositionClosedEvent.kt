package com.piotr.marketbroker.application.event

import com.piotr.marketbroker.domain.accountdetails.PositionsRecord

class PositionClosedEvent (val positions: List<PositionsRecord>)
