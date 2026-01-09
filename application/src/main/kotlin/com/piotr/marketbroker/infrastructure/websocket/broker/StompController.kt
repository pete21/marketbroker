package com.piotr.marketbroker.infrastructure.websocket.broker

import com.piotr.marketbroker.common.logger
import org.springframework.messaging.handler.annotation.MessageMapping
import org.springframework.messaging.handler.annotation.SendTo
import org.springframework.messaging.simp.SimpMessageHeaderAccessor
import org.springframework.stereotype.Controller


@Controller
class StompController {

    private val log by logger()

    @MessageMapping("/addUser")
    @SendTo("/topic/public")
    fun message(message: ChatMessageObject, headerAccessor: SimpMessageHeaderAccessor): ChatMessageObject {
        // Add username in web socket session
        headerAccessor.sessionAttributes?.put("username", message.sender)
        log.info("New user: ${message.sender}" )
        return message
    }

    @MessageMapping("/sendMessage")
    @SendTo("/topic/public")
    fun sendMessage(message: ChatMessageObject): ChatMessageObject {
        return message
    }
}
