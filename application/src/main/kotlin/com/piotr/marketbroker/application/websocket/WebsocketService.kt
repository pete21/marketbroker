package com.piotr.marketbroker.application.websocket

import com.piotr.marketbroker.application.event.WebsocketDisconnectedEvent
import com.piotr.marketbroker.infrastructure.websocket.WebsocketSessionHandler
import mu.KotlinLogging
import org.springframework.context.event.EventListener
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Service

private val log = KotlinLogging.logger(WebsocketService::class.toString())

@Service
class WebsocketService(
    private val websocketSessionHandler: WebsocketSessionHandler
) {

    private var login: String? = null
    private var token: String? = null
    private var websocketServer: String? = null
    private var sessionState: Boolean = false
    private val subscribed = mutableSetOf<Int>()

    fun connect(login: String, token: String, websocketServer: String): Boolean {
        this.login = login
        this.token = token
        this.websocketServer = websocketServer
//        ScheduledExecutorService ex = Executors.newSingleThreadScheduledExecutor();
//        ex.schedule(() -> login(login, password, token), 1, TimeUnit.SECONDS);
        try {
            Thread.sleep(400)
            websocketSessionHandler.connect(websocketServer)
            Thread.sleep(400)
            login(login, token)
            Thread.sleep(1000)
            accountSummary()
            Thread.sleep(100)
            sessionState = true
        } catch (e: InterruptedException) {
            // TODO Auto-generated catch block
            log.error("Websocket connect failed")
            return false
        }
        return true
    }

    fun disconnect() {
        sessionState = false
        websocketSessionHandler.disconnect()
        subscribed.clear()
    }

    fun subscribe(quoteId: Int, status: Boolean) {
        if (status) {
            val msg = String.format(SUBSCRIBE, quoteId)
            websocketSessionHandler.sendMsg(msg)
            subscribed.add(quoteId)
            log.info("Subscribe: $msg")
        } else {
            val msg = String.format(UNSUBSCRIBE, quoteId)
            websocketSessionHandler.sendMsg(msg)
            subscribed.remove(quoteId)
            log.info("Unsubscribe: $msg")
        }
    }


    private fun login(login: String, token: String) {
        val msg = String.format(LOGIN, login, token)
        websocketSessionHandler.sendMsg(msg)
        log.info("Login: $msg")
    }

    private fun accountSummary() {
        websocketSessionHandler.sendMsg(ACCOUNT_SUMMARY)
        log.info("Account summary: $ACCOUNT_SUMMARY")
    }

    @Async
    @EventListener
    fun handleDisconnect(event: WebsocketDisconnectedEvent) {
        log.info("handle WebsocketDisconnectedEvent")
        if (sessionState) {
            connect(login!!, token!!, websocketServer!!)
            subscribed.forEach { subscribe(it, true) }
        }
    }

    private companion object {
        private const val SUBSCRIBE = "{\"quoteId\":%d,\"priceGrouping\":\"Sampled\",\"action\":\"subscribe\"}"

        private const val UNSUBSCRIBE = "{\"quoteId\":%d,\"priceGrouping\":\"Sampled\",\"action\":\"unsubscribe\"}"

        private const val LOGIN =
            "{\"action\":\"authentication\",\"loginId\":\"%s\",\"tradingAccountType\":\"SPREAD\",\"token\":\"%s\"}"

        private const val ACCOUNT_SUMMARY =
            "{\"data\":\"{\\\"SubscribeToAccountSummary\\\":true,\\\"SubscribeToAccountDetails\\\":true}\",\"action\":\"options\"}"
    }
}
