package com.piotr.marketbroker.application.usecase.exception

import java.util.*

class AccessToMessageDenied(messageId: UUID) : RuntimeException("Access to message: $messageId denied")
