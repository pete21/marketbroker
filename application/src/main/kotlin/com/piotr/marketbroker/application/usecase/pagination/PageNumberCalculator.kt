package com.piotr.marketbroker.application.usecase.pagination

import org.springframework.stereotype.Component
import kotlin.math.roundToInt

@Component
class PageNumberCalculator {
    fun calculatePageNumber(limit: Int, offset: Int): Int {
        return offset / limit.toDouble().roundToInt()
    }
}
