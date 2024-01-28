package com.piotr.marketbroker.application.event

import com.piotr.marketbroker.application.websocket.message.OpeningOrdersRecord
import com.piotr.marketbroker.application.websocket.message.PositionsRecord

class AccountDetailsEvent (val positions: List<PositionsRecord>, val openingOrders: List<OpeningOrdersRecord>)
