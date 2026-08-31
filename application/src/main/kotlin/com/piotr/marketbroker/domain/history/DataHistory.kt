package com.piotr.marketbroker.domain.history

data class DataHistory(
    val ohlc: List<Ohlc>,
)

data class Ohlc(
    val o: Float,
    val h: Float,
    val l: Float,
    val c: Float,
    val t: Int,
    val v: Float? = null,
)
