package com.piotr.marketbroker.application.usecase.exception

import java.util.*

class MessageNotFoundException(messageId: UUID) : NoSuchElementException("Message with id $messageId not found")
