package com.piotr.marketbroker.application.event.handler

import com.piotr.marketbroker.application.event.AccountSummaryEvent
import com.piotr.marketbroker.application.event.SessionClosedEvent
import com.piotr.marketbroker.application.mapper.AccountSummaryMapper.mapToAccountSummaryResponseDto
import com.piotr.marketbroker.application.model.AccountSummaryResponseDTO
import com.piotr.marketbroker.application.websocket.message.AccountSummaryDto
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

    fun getSummary(): AccountSummaryResponseDTO? {
        if (accountSummary==null) {
            return null
        }
        return mapToAccountSummaryResponseDto(accountSummary!!)
    }

    @Async
    @EventListener
    fun removeSubscriptions(event: SessionClosedEvent) {
        accountSummary = null
    }

}
