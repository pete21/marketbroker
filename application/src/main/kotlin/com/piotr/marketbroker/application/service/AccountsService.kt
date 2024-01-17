package com.piotr.marketbroker.application.service

import com.piotr.marketbroker.application.mapper.AccountMapper
import com.piotr.marketbroker.application.model.AccountResponseDTO
import com.piotr.marketbroker.infrastructure.persistence.account.SpringDataAccountRepository
import org.springframework.stereotype.Service

@Service
class AccountsService(
    private val springDataAccountRepository: SpringDataAccountRepository
) {
    fun getAccounts(): List<AccountResponseDTO> {

        val accounts = springDataAccountRepository.findAll()

        return accounts.stream().map { AccountMapper.mapToAccountResponseDto(it) }.toList()
    }

}
