package com.piotr.marketbroker.infrastructure.persistence.account

import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table

@Entity
@Table(name = "accounts")
data class Account (

    @Id
    val id: Int=0,
    val accountId: Int=0,
    val platform: String="",

    val account: String="",
    val backend: String="",
    val accountType: String="",
    val currency: String="",
    val currencySymbol: String="",
    val balance: String="",
    val equity: String="",

    val ct_login_id: String="",
    val ct_login_password: String=""

)
