package com.piotr.marketbroker.application.mapper

import com.piotr.marketbroker.application.model.PositionResponseDTO
import com.piotr.marketbroker.infrastructure.persistence.accountdetailsposition.AccountDetailsPosition

object PositionMapper {

    fun mapToPositionResponseDto(accountDetailsPosition: AccountDetailsPosition) = PositionResponseDTO (
            positionId = accountDetailsPosition.positionID,
            marketId = accountDetailsPosition.marketID,
            quoteId = accountDetailsPosition.quoteID,
            type = accountDetailsPosition.type,
            direction = accountDetailsPosition.direction,
            creationTimeUTC = accountDetailsPosition.creationTimeUTC,
            stake = accountDetailsPosition.stake,
            openingPriceDecimal = accountDetailsPosition.openingPrice,
            currentPriceDecimal = accountDetailsPosition.currentPrice,
            openPL = accountDetailsPosition.openPL,
            stopOrderPrice = accountDetailsPosition.stopOrderPrice.toString(),
            limitOrderPrice = accountDetailsPosition.limitOrderPrice.toString(),
            imr = 0F,
            prcGenDecimalPlaces = accountDetailsPosition.prcGenDecimalPlaces,
            betPer = accountDetailsPosition.betPer,
            tradable = accountDetailsPosition.tradable,
            isRollingMarket = accountDetailsPosition.isRollingMarket,
            isTriggered = accountDetailsPosition.isTriggered,
            currencyCode = accountDetailsPosition.currencyCode,
            isTotal = accountDetailsPosition.isTotal
    )
}
