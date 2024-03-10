package com.piotr.marketbroker.infrastructure.persistence.order

import jakarta.persistence.CascadeType
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.OneToOne
import jakarta.persistence.Table
import java.time.LocalDateTime

@Entity
@Table(name = "orders")
class Order(

    @Id @GeneratedValue val id: Int=0,
    val marketId: Int=0,
    val quoteId: Int=0,
    val price: Float=0f,
    val stake: Int=0,                //orderStake
    val direction: Int=0,             //-1 - sell; 1-buy      -> "tradeModeID" false-buy true-sell
    val orderModeID: Int=0,           //orderModeID: 0 - market order, 1 - limit order, 2 - stop order
    val limitOrderPrice: Float=0f,
    val stopOrderPrice: Float=0f,
//     float IDOLimitOrderPrice;
//     float IDOStopOrderPrice;
    val trailingPoint: Int=0,
    val createdAt: LocalDateTime = LocalDateTime.now(),

    @Transient
    val key: String?=null,

    @OneToOne(cascade = [CascadeType.ALL])
    @JoinColumn(name = "traderequests_id", referencedColumnName = "id")
    var tradeRequest: TradeRequest? = null,

    @OneToOne(cascade = [CascadeType.ALL])
    @JoinColumn(name = "openorderrequests_id", referencedColumnName = "id")
    var openOrderResponse: OpenOrderResponse? = null,

    val positionId: Int=0
)

