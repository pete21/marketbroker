package com.piotr.marketbroker.infrastructure.websocket

import org.springframework.web.socket.WebSocketSession
import kotlin.concurrent.Volatile


class WebsocketSessionHolder(private var session: WebSocketSession?) {

    private var createTime: Long = 0

    @Volatile
    private var hasHandledMessages = false

    init {
        this.createTime = System.currentTimeMillis()
    }

    fun getSession(): WebSocketSession? {
        return this.session
    }

    fun getCreateTime(): Long {
        return this.createTime
    }

    fun setHasHandledMessages() {
        this.hasHandledMessages = true
    }

    fun hasHandledMessages(): Boolean {
        return this.hasHandledMessages
    }

    override fun toString(): String {
        return "WebSocketSessionHolder[session=" + this.session + ", createTime=" +
                this.createTime + ", hasHandledMessages=" + this.hasHandledMessages + "]"
    }
}
