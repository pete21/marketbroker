package com.piotr.marketbroker.application.event

class SubscriptionEvent (
    val quoteId: Int,
    val action: String,
    val status: Boolean
)