package com.piotr.marketbroker.infrastructure.persistence.marketquote

import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.Id
import jakarta.persistence.Table

@Entity
@Table(name = "marketquote")
class MarketQuote (

    @Id @GeneratedValue var id: Int? = null,

    var type: String? = null,

    var marketID: Int = 0,

var quoteID: Int = 0,

var atQuoteAtMarket: Int = 0,

var exchangeID: Int = 0,

var prcGenFractionalPrice: Int = 0,

var prcGenDecimalPlaces: Int = 0,

var high: Int = 0,

var low: Int = 0,

var dailyChange: Int = 0,

var bid: Int = 0,

var ask: Int = 0,

var betPer: Int = 0,

var isGSLPercent: Int = 0,

var gSLDis: Int = 0,

var minCloseOrderDisTicks: Int = 0,

var minOpenOrderDisTicks: Int = 0,

var displayBetPer: Int = 0,

var isInPortfolio:Boolean = false,

var tradable:Boolean = false,

var tradeOnWeb:Boolean = false,

var callOnly:Boolean = false,

var marketName: String? = null,

var tradeStartTime: String? = null,

var currency: String? = null,

var allowGtdsStops: Int = 0,

var forceOpen: Boolean = false,

var margin: Float = 0f,

var marginType: Boolean = false,

var gSLCharge: Int = 0,

var isGSLChargePercent: Int = 0,

var spread: Int = 0,

var tradeRateType: Int = 0,

var openTradeRate: Int = 0,

var closeTradeRate: Int = 0,

var minOpenTradeRate: Int = 0,

var minCloseTradeRate: Int = 0,

var priceDecimal: Int = 0,

var subscription: Boolean = false,

var superGroupID: Int = 0

)