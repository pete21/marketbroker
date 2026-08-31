package com.piotr.marketbroker.infrastructure.rest.controller.mapper

import com.piotr.marketbroker.application.model.HistoryResponseDTO
import com.piotr.marketbroker.application.model.OHLC
import com.piotr.marketbroker.domain.history.DataHistory

object HistoryMapper {
    fun mapHistoryToHistoryResponseDto(history: DataHistory) = HistoryResponseDTO(
        ohlc = history.ohlc.map { OHLC(
            o = it.o,
            h = it.h,
            l = it.l,
            c = it.c,
            v = it.v,
            t = it.t,
        ) },
    )

}