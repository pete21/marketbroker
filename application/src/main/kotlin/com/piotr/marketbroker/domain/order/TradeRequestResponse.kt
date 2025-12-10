package com.piotr.marketbroker.domain.order

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.OneToOne
import jakarta.persistence.Table

@Entity
@Table(name="trade_request_responses")
@JsonIgnoreProperties(ignoreUnknown = true)
data class TradeRequestResponse (
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    val id: Int? = null,

    @JsonProperty("__type")
    val type: String="",

    @JsonProperty("MarketID")
    val marketId: Int=0,

    @JsonProperty("Direction")
    val direction: String="",

    @JsonProperty("Market")
    val market: String? = null,

    @JsonProperty("ExpiryDate")
    val expiryDate: String="",

    @JsonProperty("Price")
    val price: Float=0f,

    @JsonProperty("Stake")
    val stake: Float=0f,

    @JsonProperty("TradeStatus")
    val tradeStatus: String? = null,

    @JsonProperty("PositionID")
    val positionId: Int=0,

    @JsonProperty("ReferralID")
    val referralId: String? = null,

//    @JsonProperty("CloseBets")
//    @JsonIgnore
//    @Transient
//    val closeBets: String? = null,

    @JsonProperty("OrderMode")
    val orderMode: String="",

    @JsonProperty("OrderType")
    val orderType: String="",

    @JsonProperty("StopOrderPrice")
    val stopOrderPrice: String="",

    @JsonProperty("LimitOrderPrice")
    val limitOrderPrice: String="",

    @JsonProperty("OrderID")
    val orderId: String="",

    @JsonProperty("Status")
    val status: Int? = null,

    @JsonProperty("Message")
    val message: String? = null,

    @OneToOne(mappedBy = "tradeRequestResponse")
    val order: Order?=null
)
