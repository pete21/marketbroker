package com.piotr.marketbroker.configuration.websocket

import org.springframework.context.annotation.Configuration
import org.springframework.messaging.simp.config.MessageBrokerRegistry
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker
import org.springframework.web.socket.config.annotation.StompEndpointRegistry
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer

@Configuration
@EnableWebSocketMessageBroker
class WebsocketConfiguration : WebSocketMessageBrokerConfigurer {
// WebSocket endpoint registration
    override fun registerStompEndpoints(stompEndpointRegistry: StompEndpointRegistry) {
        stompEndpointRegistry
            .addEndpoint("/ws")
            .setAllowedOriginPatterns("*")
            .withSockJS()
//            .setHeartbeatTime(10_000)
    }
    override fun configureMessageBroker(config: MessageBrokerRegistry) {
        config.setApplicationDestinationPrefixes("/app")
        config.enableSimpleBroker("/topic")
    }

//    override fun configureMessageConverters(list: MutableList<MessageConverter?>): Boolean {
//        val converter = MappingJackson2MessageConverter()
//        list.add(converter)
//        return true
//    }
}