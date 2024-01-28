package com.piotr.marketbroker.infrastructure.persistence.marketquote

import com.fasterxml.jackson.annotation.JsonProperty
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.Id
import jakarta.persistence.Table

@Entity
@Table(name = "marketquote")
class MarketQuote (

    @Id @GeneratedValue val id: Int = 0,

    @JsonProperty("__type")
    val type: String = "",

    @JsonProperty("MarketID")
    val marketID: Int = 0,

    @JsonProperty("QuoteID")
    val quoteID: Int = 0,

    @JsonProperty("AtQuoteAtMarket")
    val atQuoteAtMarket: Int = 0,

    @JsonProperty("ExchangeID")
    val exchangeID: Int = 0,

    @JsonProperty("PrcGenFractionalPrice")
    val prcGenFractionalPrice: Int = 0,

    @JsonProperty("PrcGenDecimalPlaces")
    val prcGenDecimalPlaces: Int = 0,

    @JsonProperty("High")
    val high: Int = 0,

    @JsonProperty("Low")
    val low: Int = 0,

    @JsonProperty("DailyChange")
    val dailyChange: Int = 0,

    @JsonProperty("Bid")
    val bid: Int = 0,

    @JsonProperty("Ask")
    val ask: Int = 0,

    @JsonProperty("BetPer")
    val betPer: Int = 0,

    @JsonProperty("IsGSLPercent")
    val isGSLPercent: Int = 0,

    @JsonProperty("GSLDis")
    val gSLDis: Int = 0,

    @JsonProperty("MinCloseOrderDisTicks")
    val minCloseOrderDisTicks: Int = 0,

    @JsonProperty("MinOpenOrderDisTicks")
    val minOpenOrderDisTicks: Int = 0,

    @JsonProperty("DisplayBetPer")
    val displayBetPer: Int = 0,

    @JsonProperty("IsInPortfolio")
    val isInPortfolio:Boolean = false,

    @JsonProperty("Tradable")
    val tradable:Boolean = false,

    @JsonProperty("TradeOnWeb")
    val tradeOnWeb:Boolean = false,

    @JsonProperty("CallOnly")
    val callOnly:Boolean = false,

    @JsonProperty("MarketName")
    val marketName: String? = null,

    @JsonProperty("TradeStartTime")
    val tradeStartTime: String? = null,

    @JsonProperty("Currency")
    val currency: String? = null,

    @JsonProperty("AllowGtdsStops")
    val allowGtdsStops: Int = 0,

    @JsonProperty("ForceOpen")
    val forceOpen: Boolean = false,

    @JsonProperty("Margin")
    val margin: Float = 0f,

    @JsonProperty("MarginType")
    val marginType: Boolean = false,

    @JsonProperty("GSLCharge")
    val gSLCharge: Int = 0,

    @JsonProperty("IsGSLChargePercent")
    val isGSLChargePercent: Int = 0,

    @JsonProperty("Spread")
    val spread: Int = 0,

    @JsonProperty("TradeRateType")
    val tradeRateType: Int = 0,

    @JsonProperty("OpenTradeRate")
    val openTradeRate: Int = 0,

    @JsonProperty("CloseTradeRate")
    val closeTradeRate: Int = 0,

    @JsonProperty("MinOpenTradeRate")
    val minOpenTradeRate: Int = 0,

    @JsonProperty("MinCloseTradeRate")
    val minCloseTradeRate: Int = 0,

    @JsonProperty("PriceDecimal")
    val priceDecimal: Int = 0,

    @JsonProperty("Subscription")
    val subscription: Boolean = false,

    @JsonProperty("SuperGroupID")
    val superGroupID: Int = 0

) {
    override fun toString(): String {
        return "MarketQuote(id=$id, type='$type', marketID=$marketID, quoteID=$quoteID, atQuoteAtMarket=$atQuoteAtMarket, exchangeID=$exchangeID, prcGenFractionalPrice=$prcGenFractionalPrice, prcGenDecimalPlaces=$prcGenDecimalPlaces, high=$high, low=$low, dailyChange=$dailyChange, bid=$bid, ask=$ask, betPer=$betPer, isGSLPercent=$isGSLPercent, gSLDis=$gSLDis, minCloseOrderDisTicks=$minCloseOrderDisTicks, minOpenOrderDisTicks=$minOpenOrderDisTicks, displayBetPer=$displayBetPer, isInPortfolio=$isInPortfolio, tradable=$tradable, tradeOnWeb=$tradeOnWeb, callOnly=$callOnly, marketName=$marketName, tradeStartTime=$tradeStartTime, currency=$currency, allowGtdsStops=$allowGtdsStops, forceOpen=$forceOpen, margin=$margin, marginType=$marginType, gSLCharge=$gSLCharge, isGSLChargePercent=$isGSLChargePercent, spread=$spread, tradeRateType=$tradeRateType, openTradeRate=$openTradeRate, closeTradeRate=$closeTradeRate, minOpenTradeRate=$minOpenTradeRate, minCloseTradeRate=$minCloseTradeRate, priceDecimal=$priceDecimal, subscription=$subscription, superGroupID=$superGroupID)"
    }
}


/*
{
  "__type": "TradingPlatform.Market",
  "MarketID": 17068,
  "QuoteID": 6374,
  "AtQuoteAtMarket": 1,
  "ExchangeID": 155,
  "PrcGenFractionalPrice": 0,
  "PrcGenDecimalPlaces": 1,
  "High": 0,
  "Low": 0,
  "DailyChange": 0,
  "Bid": 0,
  "Ask": 0,
  "BetPer": 1,
  "IsGSLPercent": 1,
  "GSLDis": 2,
  "MinCloseOrderDisTicks": 2,
  "MinOpenOrderDisTicks": 2,
  "DisplayBetPer": 1,
  "IsInPortfolio": false,
  "Tradable": false,
  "TradeOnWeb": true,
  "CallOnly": false,
  "MarketName": "Germany 40 - Rolling Cash",
  "TradeStartTime": "",
  "Currency": "EUR",
  "AllowGtdsStops": 1,
  "ForceOpen": true,
  "Margin": 0.5,
  "MarginType": false,
  "GSLCharge": 3,
  "IsGSLChargePercent": 0,
  "Spread": 6,
  "TradeRateType": 0,
  "OpenTradeRate": 0,
  "CloseTradeRate": 0,
  "MinOpenTradeRate": 0,
  "MinCloseTradeRate": 0,
  "PriceDecimal": 1,
  "Subscription": false,
  "SuperGroupID": 1
}
*/