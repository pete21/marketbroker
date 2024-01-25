package com.piotr.marketbroker.infrastructure.persistence.tick

import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.Id
import jakarta.persistence.Table
import jakarta.persistence.Transient

@Entity
@Table(name="tick")
class Tick(

    @Id @GeneratedValue var id: Int? = null,
    val quoteId: Int,
    val bid: Float,
    val ask: Float,
    val mid: Float,
    val time: Int,
    val millis: Int,

    @Transient
    val longtime: Long,

    @Transient
    private val key: String
)
/*
    init {
        var t = t
        quoteId = q
        bid = b
        ask = a
        mid = m
        t /= 10000
        longtime = t
        millis = (t % 1000).toInt()
        time = (t / 1000 - 62135596800L).toInt() //convert TD365 timestamp to unix timestamp
        key = k
    }

 */

