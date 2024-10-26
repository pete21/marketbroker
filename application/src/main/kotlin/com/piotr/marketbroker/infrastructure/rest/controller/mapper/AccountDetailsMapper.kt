package com.piotr.marketbroker.infrastructure.rest.controller.mapper

import com.piotr.marketbroker.application.model.OpeningOrderResponseDTO
import com.piotr.marketbroker.application.model.PositionResponseDTO
import com.piotr.marketbroker.domain.accountdetails.OpeningOrdersRecord
import com.piotr.marketbroker.domain.accountdetails.PositionsRecord
import java.time.ZoneOffset

object AccountDetailsMapper {

    fun mapToPositionResponseDto(positionsRecord: PositionsRecord) = PositionResponseDTO (
            positionId = positionsRecord.positionId,
            marketId = positionsRecord.marketId,
            quoteId = positionsRecord.quoteId,
            type = positionsRecord.type,
            direction = positionsRecord.direction,
            creationTimeUTC = positionsRecord.creationTimeUTC.atOffset(ZoneOffset.UTC),
            stake = positionsRecord.stake,
            openingPriceDecimal = positionsRecord.openingPriceDecimal,
            currencyCode = positionsRecord.currencyCode,
            currentPriceDecimal = positionsRecord.currentPriceDecimal,
            openPL = positionsRecord.openPl,
            stopOrderPrice = if (positionsRecord.stopOrderPrice=="-") 0f else positionsRecord.stopOrderPrice.toFloat(),
            limitOrderPrice = if (positionsRecord.limitOrderPrice=="-") 0f else positionsRecord.limitOrderPrice.toFloat(),
            imr = positionsRecord.imr,
            prcGenDecimalPlaces = positionsRecord.prcGenDecimalPlaces,
            betPer = positionsRecord.betPer,
            tradable = positionsRecord.tradable,
            isRollingMarket = positionsRecord.isRollingMarket,
            isTriggered = positionsRecord.isTriggered,
            isTotal = positionsRecord.isTotal
    )

        fun mapToOpeningOrderResponseDto(openingOrderRecord: OpeningOrdersRecord) = OpeningOrderResponseDTO(
                currentPrice = openingOrderRecord.currentPrice,
                direction = if (openingOrderRecord.direction=="Sell") -1 else 1,
                expiryDate = openingOrderRecord.expiryDate,
                goodTill = openingOrderRecord.goodTill,
                iDOLimitOrderPrice = openingOrderRecord.iDOLimitOrderPrice.toFloatOrNull()?:0f,
                iDOStopOrderPrice = openingOrderRecord.iDOStopOrderPrice.toFloatOrNull()?:0f,
                isTriggered = openingOrderRecord.isTriggered,
                limitOrderPrice = openingOrderRecord.limitOrderPrice.toFloatOrNull()?:0f,
                margin = openingOrderRecord.margin,
                market = openingOrderRecord.market,
                marketId = openingOrderRecord.marketId,
                marketTradable = openingOrderRecord.marketTradable,
                orderId = openingOrderRecord.orderId,
                period = openingOrderRecord.period,
                creationTimeUTC = openingOrderRecord.creationTimeUTC.toString(),
                quoteId = openingOrderRecord.quoteId,
                quoteMode = openingOrderRecord.quoteMode,
                stake = openingOrderRecord.stake,
                status = openingOrderRecord.status,
                stopOrderPrice = openingOrderRecord.stopOrderPrice.toFloatOrNull()?:0f,
                type = openingOrderRecord.type,
                trailingPoint = openingOrderRecord.trailingPoint==1,
                isGuarantee = openingOrderRecord.isGuarantee,
                isForceOpen = openingOrderRecord.isForceOpen,
                orderPriceModeEnum = openingOrderRecord.orderPriceModeEnum,
                currencySymbol = openingOrderRecord.currencySymbol
        )

        /*
        fun mapToOrderResponseDto(openingOrdersRecord: OpeningOrdersRecord) = OpeningOrderResponseDTO (

                orderId = openingOrdersRecord.orderID,
                currency = openingOrdersRecord.currency,
                currentPrice = openingOrdersRecord.currentPrice,
                direction = openingOrdersRecord.direction,
                expiryDate = openingOrdersRecord.expiryDate,
                goodTill = openingOrdersRecord.goodTill,
                iDOLimitOrderPrice = openingOrdersRecord.iDOLimitOrderPrice.toFloat(),
                iDOStopOrderPrice = openingOrdersRecord.iDOStopOrderPrice.toFloat(),
                iDOGuaranteed = openingOrdersRecord.iDOGuaranteed,
                isTriggered = openingOrdersRecord.isTriggered,
                limitOrderPrice = openingOrdersRecord.limitOrderPrice,
                margin = openingOrdersRecord.margin,
                marketID = openingOrdersRecord.marketID,
                marketTradable = openingOrdersRecord.marketTradable,
                period = openingOrdersRecord.period,
                creationTimeUTC= openingOrdersRecord.creationTimeUTC,
                quoteId = openingOrdersRecord.quoteId,
                quoteMode = openingOrdersRecord.quoteMode,
                stake = openingOrdersRecord.stake,
                status = openingOrdersRecord.status,
                stopOrderPrice = openingOrdersRecord.stopOrderPrice,
                type = openingOrdersRecord.type,
                trailingPoint = openingOrdersRecord.trailingPoint,
                isGuarantee = openingOrdersRecord.isGuarantee,
                isForceOpen = openingOrdersRecord.isForceOpen,
                orderPriceModeEnum = openingOrdersRecord.orderPriceModeEnum,
                currencySymbol = openingOrdersRecord.currencySymbol

        )

         */
}
