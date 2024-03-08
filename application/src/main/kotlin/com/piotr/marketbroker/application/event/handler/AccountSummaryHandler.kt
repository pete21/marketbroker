package com.piotr.marketbroker.application.event.handler

import com.piotr.marketbroker.application.event.AccountSummaryEvent
import com.piotr.marketbroker.application.mapper.AccountSummaryMapper.mapToAccountSummaryResponseDto
import com.piotr.marketbroker.application.model.AccountSummaryResponseDTO
import com.piotr.marketbroker.application.websocket.message.AccountSummaryDto
import org.springframework.context.event.EventListener
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Service

@Service
class AccountSummaryHandler {
    private lateinit var accountSummary: AccountSummaryDto

    @Async
    @EventListener
    fun handleAccountSummaryEvent(event: AccountSummaryEvent) {
        accountSummary = event.accountSummaryDto
    }

    fun getSummary(): AccountSummaryResponseDTO {
        return mapToAccountSummaryResponseDto(accountSummary)
    }

}
