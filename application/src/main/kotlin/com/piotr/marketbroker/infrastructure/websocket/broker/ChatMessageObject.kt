package com.piotr.marketbroker.infrastructure.websocket.broker


class ChatMessageObject(
    val sender: String = "",
    val content: String = "",
    val messageType: MessageType = MessageType.JOIN
)


enum class MessageType {
    JOIN, LEAVE, CHAT
}
