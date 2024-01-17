package com.piotr.marketbroker.application.mapper

import com.piotr.marketbroker.application.model.AccountResponseDTO
import com.piotr.marketbroker.infrastructure.persistence.account.Account

object AccountMapper {

    fun mapToAccountResponseDto(account: Account) = AccountResponseDTO (
        accountId = account.accountId,
        platform = account.platform,
        account = account.account,
        backend = account.backend,
        accountType = account.accountType,
        currency = account.currency,
        currencySymbol = account.currencySymbol,
        balance = account.balance,
        equity = account.equity,
        ctLoginId = account.ct_login_id,
        ctLoginPassword = account.ct_login_password
    )
}
