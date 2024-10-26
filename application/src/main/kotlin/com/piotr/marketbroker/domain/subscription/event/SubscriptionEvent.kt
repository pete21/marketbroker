package com.piotr.marketbroker.domain.subscription.event

class SubscriptionEvent (val quoteId: Int, val action: String, val status: Boolean)
