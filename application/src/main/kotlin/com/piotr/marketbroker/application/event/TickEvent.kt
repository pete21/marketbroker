package com.piotr.marketbroker.application.event

class TickEvent (val ticks: List<TickData>)

class TickData(q: Int, b: Float, a: Float, m: Float, t: Long, k: String) {

    private val quoteId: Int
    private val bid: Float
    private val ask: Float
    private val mid: Float
    private val time: Int
    private val millis: Int
    private val longtime: Long
    private val key: String

    init {
        var tt: Long = t
        quoteId = q
        bid = b
        ask = a
        mid = m
        tt /= 10000
        longtime = t
        (tt % 1000).toInt().also { millis = it }
        (tt / 1000 - 62135596800L).toInt().also { time = it } //convert TD365 timestamp to unix timestamp
        key = k
    }
}
