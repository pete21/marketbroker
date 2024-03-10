package com.piotr.marketbroker.application.mapper

import com.piotr.marketbroker.application.model.AccountSummaryResponseDTO
import com.piotr.marketbroker.application.websocket.message.AccountSummaryDto

object AccountSummaryMapper {

    fun mapToAccountSummaryResponseDto(account: AccountSummaryDto) = AccountSummaryResponseDTO (
        accountId = if (account.accountId==null) 0 else account.accountId.toInt(),

        platformId = account.platformId,

        accountValuation = account.accountValuation ?: 0f,

        fundedPercentage = account.fundedPercentage,

        margin = account.margin ?: 0,

        openPnLQuote = account.openPnLQuote ?: 0,

        accountBalance = account.accountBalance ?: 0f,

        credit = account.credit ?: 0f,

        waivedMargin = account.waivedMargin ?: 0f,

        resources = account.resources ?: 0f,

        changeIMR = account.changeIMR ?: 0f,

        variationMarginRequired = 0f,

        marginPercent = account.marginPercent ?: 0f,

        calculatedUTCTicks = account.calculatedUTCTicks ?: 0L,

        hasError = account.hasError ?: false

    )
}
