package com.piotr.marketbroker.application.service

import com.fasterxml.jackson.annotation.JsonAutoDetect
import com.fasterxml.jackson.annotation.PropertyAccessor
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.piotr.marketbroker.domain.order.TradeRequestResponse
import io.kotest.core.spec.style.ExpectSpec
import io.kotest.matchers.shouldBe

class TradeRequestResponseMappingTest : ExpectSpec({

    val mapper = jacksonObjectMapper()
        .registerModule(JavaTimeModule())
        .setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY)
        .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)

    context("TradeRequestResponse Jackson mapping") {
        expect("should map JSON string to TradeRequestResponse") {
            val testValue = """
                {"__type":"TradingPlatform.TradeRequest","MarketID":17068,"Direction":"sell","Market":"Germany 40","ExpiryDate":"31/12/30","Price":26374.6,"Stake":0.5,"TradeStatus":null,"PositionID":27299159,"ReferralID":"0","CloseBets":{"ProfitLoss":null,"ClosedBet":[{"ReferenceNo":null,"OpenPrice":"26362.1","ProfitLoss":"6.25"}]},"OrderMode":"","OrderType":"","StopOrderPrice":"","LimitOrderPrice":"","OrderID":"","Status":0,"Message":null}
            """.trimIndent()

            val tradeRequestResponse: TradeRequestResponse = mapper.readValue(testValue)

            tradeRequestResponse shouldBe TradeRequestResponse(
                type = "TradingPlatform.TradeRequest",
                marketId = 17068,
                direction = "sell",
                market = "Germany 40",
                expiryDate = "31/12/30",
                price = 26374.6f,
                stake = 0.5f,
                tradeStatus = null,
                positionId = 27299159,
                referralId = "0",
                orderMode = "",
                orderType = "",
                stopOrderPrice = "",
                limitOrderPrice = "",
                orderId = "",
                status = 0,
                message = null
            )
        }



        expect("should map JSON string to TradeRequestResponse with close price") {
            val testValue = """{"d":{"__type":"TradingPlatform.TradeRequest","MarketID":17068,"Direction":"sell","Market":"Germany 40","ExpiryDate":"31/12/30","Price":26327.9,"Stake":0.5,"TradeStatus":null,"PositionID":27300974,"ReferralID":"0","CloseBets":{"ProfitLoss":null,"ClosedBet":[{"ReferenceNo":null,"OpenPrice":"26319.4","ProfitLoss":"4.25"}]},"OrderMode":"","OrderType":"","StopOrderPrice":"","LimitOrderPrice":"","OrderID":"","Status":0,"Message":null}}"""

            val tradeRequestResponse: TradeRequestResponse = mapper.readValue(testValue)

            tradeRequestResponse shouldBe TradeRequestResponse(
                type = "TradingPlatform.TradeRequest",
                marketId = 17068,
                direction = "sell",
                market = "Germany 40",
                expiryDate = "31/12/30",
                price = 26327.9f,
                stake = 0.5f,
                tradeStatus = null,
                positionId = 27300974,
                referralId = "0",
                orderMode = "",
                orderType = "",
                stopOrderPrice = "",
                limitOrderPrice = "",
                orderId = "",
                status = 0,
                message = null
            )
        }
    }
})
