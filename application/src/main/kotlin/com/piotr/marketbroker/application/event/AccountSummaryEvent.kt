package com.piotr.marketbroker.application.event

import com.piotr.marketbroker.application.websocket.message.AccountSummaryDto

class AccountSummaryEvent(val accountSummaryDto: AccountSummaryDto)