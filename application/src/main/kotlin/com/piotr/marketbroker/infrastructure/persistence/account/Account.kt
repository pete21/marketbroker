package com.piotr.marketbroker.infrastructure.persistence.account

import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.Id
import jakarta.persistence.Table

@Entity
@Table(name = "accounts")
class Account (

    @Id @GeneratedValue var id: Int? = null,
    var accountId: Int = 0,
    var platform: String? = null,

    var account: String? = null,
    var backend: String? = null,
    var accountType: String? = null,
    var currency: String? = null,
    var currencySymbol: String? = null,
    var balance: String? = null,
    var equity: String? = null,

    var ct_login_id: String? = null,
    var ct_login_password: String? = null

)
