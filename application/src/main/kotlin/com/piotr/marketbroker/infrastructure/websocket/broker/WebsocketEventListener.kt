package com.piotr.marketbroker.infrastructure.websocket.broker

import com.piotr.marketbroker.common.logger
import org.springframework.context.event.EventListener
import org.springframework.messaging.simp.SimpMessageSendingOperations
import org.springframework.messaging.simp.stomp.StompHeaderAccessor
import org.springframework.stereotype.Component
import org.springframework.web.socket.messaging.SessionConnectedEvent
import org.springframework.web.socket.messaging.SessionDisconnectEvent

@Component
class WebsocketEventListener(
    private val messagingTemplate: SimpMessageSendingOperations
) {

    private val log by logger()

    @EventListener
    fun handleWebSocketConnectListener(event: SessionConnectedEvent?) {
        log.info("Received a new websocket connection")
        log.info(event.toString())
    }

    @EventListener
    fun handleWebSocketDisconnectListener(event: SessionDisconnectEvent) {
        val headerAccessor = StompHeaderAccessor.wrap(event.message)
        val username = headerAccessor.sessionAttributes!!["username"] as String?

        if (username != null) {
            log.info("User disconnected: {}", username)

            val chatMessageObject = ChatMessageObject(username, "BYE", MessageType.LEAVE)

            messagingTemplate.convertAndSend("/topic/public", chatMessageObject)
        }
    }
}
