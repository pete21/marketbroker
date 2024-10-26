package com.piotr.marketbroker.domain.accountsummary.handler

import com.piotr.marketbroker.domain.accountsummary.event.AccountSummaryEvent
import com.piotr.marketbroker.application.event.SessionClosedEvent
import com.piotr.marketbroker.domain.accountsummary.AccountSummaryDto
import org.springframework.context.event.EventListener
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Service

@Service
class AccountSummaryHandler {
    private var accountSummary: AccountSummaryDto? = null

    @Async
    @EventListener
    fun handleAccountSummaryEvent(event: AccountSummaryEvent) {
        if (event.accountSummaryDto.platformId!=0) {
            accountSummary = event.accountSummaryDto
        }
    }

    fun getSummary(): AccountSummaryDto? {
        return accountSummary
    }

    @Async
    @EventListener
    fun clearSummary(event: SessionClosedEvent) {
        accountSummary = null
    }

}
