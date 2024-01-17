package com.piotr.marketbroker.application.mapper

import com.piotr.marketbroker.application.model.PositionResponseDTO
import com.piotr.marketbroker.infrastructure.persistence.position.Position

object PositionMapper {

    fun mapToPositionResponseDto(position: Position) = PositionResponseDTO (
            positionId = position.positionID,
            marketId = position.marketID,
            quoteId = position.quoteID,
            type = position.type,
            direction = position.direction,
            creationTimeUTC = position.creationTimeUTC,
            stake = position.stake,
            openingPriceDecimal = position.openingPrice,
            currentPriceDecimal = position.currentPrice,
            openPL = position.openPL,
            stopOrderPrice = position.stopOrderPrice.toString(),
            limitOrderPrice = position.limitOrderPrice.toString(),
            imr = 0F,
            prcGenDecimalPlaces = position.prcGenDecimalPlaces,
            betPer = position.betPer,
            tradable = position.tradable,
            isRollingMarket = position.isRollingMarket,
            isTriggered = position.isTriggered,
            currencyCode = position.currencyCode,
            isTotal = position.isTotal
    )
}
