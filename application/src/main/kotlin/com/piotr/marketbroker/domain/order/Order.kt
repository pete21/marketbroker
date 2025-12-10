package com.piotr.marketbroker.domain.order

import jakarta.persistence.CascadeType
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.OneToOne
import jakarta.persistence.Table
import java.time.LocalDateTime

@Entity
@Table(name = "orders")
data class Order(

    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    val id:  Int? = null,
    var orderId: Int=0,
    val marketId: Int=0,
    val quoteId: Int=0,
    val price: Float=0f,
    val stake: Float=0f,                //orderStake
    val direction: Int=0,             //-1 - sell; 1 - buy      -> "tradeModeID" false-buy true-sell
    val orderModeID: Int=0,           //orderModeID: 0 - market order, 1 - limit order, 2 - stop order
    val limitOrderPrice: Float=0f,
    val stopOrderPrice: Float=0f,
//     float IDOLimitOrderPrice;
//     float IDOStopOrderPrice;
    val trailingPoint: Boolean=false,
    var createdAt: LocalDateTime = LocalDateTime.now(),
    var updatedAt: LocalDateTime? = null,

    @Transient
    val key: String?=null,

    @OneToOne(cascade = [CascadeType.ALL])
    @JoinColumn(name = "trade_request_id", referencedColumnName = "id")
    var tradeRequestResponse: TradeRequestResponse? = null,

    @OneToOne(cascade = [CascadeType.ALL])
    @JoinColumn(name = "open_order_id", referencedColumnName = "id")
    var openOrderResponse: OpenOrderResponse? = null,

    var positionId: Int = 0,

    var active: Boolean = true,
    var open_price: Float=0f,
    var close_price: Float=0f,
    var open_date: LocalDateTime? = null,
    var close_date: LocalDateTime? = null,
) {

    companion object {
        fun new(
            marketId: Int,
            quoteId: Int,
            price: Float,
            stake: Float,
            direction: Int,
            orderModeID: Int,
            limitOrderPrice: Float,
            stopOrderPrice: Float,
            trailingPoint: Boolean,
            key: String?,
            positionId: Int=0,
            openPrice: Float=0f,
            closePrice: Float=0f,
            openDate: LocalDateTime? = null
        ): Order = Order(
            marketId = marketId,
            quoteId = quoteId,
            price = price,
            stake = stake,
            direction = direction,
            orderModeID = orderModeID,
            limitOrderPrice = limitOrderPrice,
            stopOrderPrice = stopOrderPrice,
            trailingPoint = trailingPoint,
            key = key,
            positionId = positionId,
            open_price = openPrice,
            close_price = closePrice,
            open_date = openDate,
        )
    }

}
