package com.piotr.marketbroker.infrastructure.persistence.tick

import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.Id
import jakarta.persistence.Table
import jakarta.persistence.Transient

@Entity
@Table(name="tick")
class Tick(

    @Id @GeneratedValue val id: Int = 0,
    val quoteId: Int = 0,
    val bid: Float = 0f,
    val ask: Float = 0f,
    val mid: Float = 0f,
    val time: Int = 0,
    val millis: Int = 0,

    @Transient
    val longtime: Long = 0,

    @Transient
    val key: String = ""
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

