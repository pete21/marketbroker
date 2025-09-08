package com.piotr.marketbroker.domain.copytrade

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table

@Entity
@Table(name="copytrade")
data class CopyTrade (
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Int? = null,
    @Column(unique=true)
    val tradeId: Int = 0,
    val ticker: String = "",
    val openTime: Int = 0,
    var type: Int = -1,
    var price: Float = 0.0f,
    var size: Float = 0.0f,
    var sl: Float = 0.0f,
    var tp: Float = 0.0f,
    var comment: String? = null,
    var status: MtTradeStatus = MtTradeStatus.NEW,
    var orderId: Int? = null,
    var positionId: Int? = null,
) {
    companion object {
        fun new(
            tradeId: Int,
            ticker: String,
            openTime: Int,
            type: Int,
            price: Float,
            size: Float,
            sl: Float,
            tp: Float,
            comment: String?,
        ) = CopyTrade(
            tradeId = tradeId,
            ticker = ticker,
            type = type,
            openTime = openTime,
            price = price,
            size = size,
            sl = sl,
            tp = tp,
            comment = comment,
        )
    }
}

enum class MtTradeStatus {
    NEW,
    SUBMITTED,
    REJECTED,
}
