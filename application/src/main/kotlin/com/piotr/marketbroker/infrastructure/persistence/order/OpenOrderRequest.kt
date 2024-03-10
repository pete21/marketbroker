package com.piotr.marketbroker.infrastructure.persistence.order

import com.fasterxml.jackson.annotation.JsonProperty
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.OneToOne
import jakarta.persistence.Table

@Entity
@Table(name="openorderrequests")
class OpenOrderRequest (

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    val id: Int=0,

    @JsonProperty("__type")
    val type: String="",
    
    @JsonProperty("OrderID")
    val orderID: String="",

    @JsonProperty("QuoteID")
    val quoteID: String?=null,

    @JsonProperty("MarketID")
    val marketID: String?=null,

    @JsonProperty("Market")
    val market: String="",

    @JsonProperty("ExpiryDate")
    val expiryDate: String="",

    @JsonProperty("TradeMode")
    val tradeMode: String="",

    @JsonProperty("Stake")
    val stake: String="",

    @JsonProperty("OrderMode")
    val orderMode: String="",

    @JsonProperty("OrderType")
    val orderType: String="",

    @JsonProperty("OrderPriceMode")
    val orderPriceMode: String="",

    @JsonProperty("LimitOrderPrice")
    val limitOrderPrice: String="",

    @JsonProperty("StopOrderPrice")
    val stopOrderPrice: String="",

    @JsonProperty("OrderStatus")
    val orderStatus: String?=null,

    @JsonProperty("IsForceOpen")
    val isForceOpen: Boolean=false,

    @JsonProperty("IDOID")
    val iDOID: String?="",

    @JsonProperty("IDOOrderMode")
    val iDOOrderMode: String?="",

    @JsonProperty("IDOTradeMode")
    val iDOTradeMode: String="",

    @JsonProperty("IDOIsGuaranteedStop")
    val iDOIsGuaranteedStop: Boolean=false,

    @JsonProperty("IDOLimitOrderPrice")
    val iDOLimitOrderPrice: String?=null,

    @JsonProperty("IDOStopOrderPrice")
    val iDOStopOrderPrice: String?=null,

    @JsonProperty("IDOTrailingPoint")
    val iDOTrailingPoint: String?=null,

    @JsonProperty("Currency")
    val currency: String="",

    @JsonProperty("IsRollingMarket")
    val isRollingMarket: Boolean=false,

    @JsonProperty("Status")
    val status: Int=0,

    @JsonProperty("Message")
    val message: String?=null,

    @OneToOne(mappedBy = "openOrderRequest")
    val order: Order?=null
)