package com.piotr.marketbroker.infrastructure.persistence.position

import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.time.LocalDateTime
import java.time.OffsetDateTime

@Entity
@Table(name = "Position")
data class Position (

    @Id
    val id: Int,

    val positionID: Int,

    val marketID: Int,

    val quoteID: Int,

    val currencySymbol: String,

    val type: String,

    val marketName: String,

    val direction: String,

    val expiryDateTime: LocalDateTime,

    val creationDateTime: LocalDateTime,

    val creationTimeUTC: OffsetDateTime,

    val stake: Int,

    val openingPrice: Float,

    val currentPrice: Float,

    val openPL: Int,

    val stopOrderPrice: Float,

    val limitOrderPrice: Float,

    val prcGenDecimalPlaces: Int,

    val betPer: Float,

    val tradable: Boolean,

    val isRollingMarket: Boolean,

    val isTriggered: Boolean,

    val currencyCode: String,

    val isTotal: Boolean
)
