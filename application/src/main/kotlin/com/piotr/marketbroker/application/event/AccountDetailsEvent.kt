package com.piotr.marketbroker.application.event

import com.piotr.marketbroker.domain.accountdetails.OpeningOrdersRecord
import com.piotr.marketbroker.domain.accountdetails.PositionsRecord

class AccountDetailsEvent (val positions: List<PositionsRecord>, val openingOrders: List<OpeningOrdersRecord>)