package com.piotr.marketbroker.infrastructure.persistence.order

import jakarta.persistence.CascadeType
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.OneToOne
import jakarta.persistence.Table

@Entity
@Table(name = "orders")
class Order(

    @Id @GeneratedValue val id: Int=0,
    val marketID: Int=0,
    val quoteID: Int=0,
    val price: Float=0f,
    val stake: Int=0,                //orderStake
    val direction: Int=0,             //-1 - sell; 1-buy      -> "tradeModeID" false-buy true-sell
//    val orderModeID: Int,           //orderModeID: 0 - market order, 1 - limit order, 2 - stop order
    val limitOrderPrice: Float=0f,
    val stopOrderPrice: Float=0f,
                                                                                    //     float IDOLimitOrderPrice;
                                                                                    //     float IDOStopOrderPrice;
    val trailingPoint: Int=0,
    val created_at: Long=0,

    @Transient
    val key: String?=null,

    @OneToOne(cascade = [CascadeType.ALL])
    @JoinColumn(name = "traderequests_id", referencedColumnName = "id")
    var tradeRequest: TradeRequest? = null,

    @OneToOne(cascade = [CascadeType.ALL])
    @JoinColumn(name = "openorderrequests_id", referencedColumnName = "id")
    var openOrderRequest: OpenOrderRequest? = null,

    val positionID: Int=0
)

