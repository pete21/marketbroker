package com.piotr.marketbroker.infrastructure.persistence.order

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonProperty
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.OneToOne
import jakarta.persistence.Table

@Entity
@Table(name="traderequests")
class TradeRequest (
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    val id: Int=0,

    @JsonProperty("__type")
    val type: String="",

    @JsonProperty("MarketID")
    val marketID: Int=0,

    @JsonProperty("Direction")
    val direction: String="",

    @JsonProperty("Market")
    val market: String="",

    @JsonProperty("ExpiryDate")
    val expiryDate: String="",

    @JsonProperty("Price")
    val price: Float=0f,

    @JsonProperty("Stake")
    val stake: Int=0,

    @JsonProperty("TradeStatus")
    val tradeStatus: String="",

    @JsonProperty("PositionID")
    val positionID: Int=0,

    @JsonProperty("ReferralID")
    val referralID: String="",

    @JsonProperty("CloseBets")
    @JsonIgnore
    val closeBets: String="",

    @JsonProperty("OrderMode")
    val orderMode: String="",

    @JsonProperty("OrderType")
    val orderType: String="",

    @JsonProperty("StopOrderPrice")
    val stopOrderPrice: String="",

    @JsonProperty("LimitOrderPrice")
    val limitOrderPrice: String="",

    @JsonProperty("OrderID")
    val orderID: String="",

    @JsonProperty("Status")
    val status: Int=0,

    @JsonProperty("Message")
    val message: String="",

    @OneToOne(mappedBy = "tradeRequest")
    val order: Order?=null
)