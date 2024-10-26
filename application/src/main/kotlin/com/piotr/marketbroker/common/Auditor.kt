package com.piotr.marketbroker.common

import java.util.UUID

data class Auditor(val customerId: UUID?, val username: String?, val type: AuditorType?)

enum class AuditorType {
    APPLICATION
}
