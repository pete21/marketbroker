package com.piotr.marketbroker.infrastructure.websocket

import com.fasterxml.jackson.annotation.JsonAutoDetect
import com.fasterxml.jackson.annotation.PropertyAccessor
import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.piotr.marketbroker.application.event.WebsocketDisconnectedEvent
import com.piotr.marketbroker.application.event.WebsocketMessageEvent
import com.piotr.marketbroker.application.websocket.message.WebsocketMessage
import com.piotr.marketbroker.common.logger
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Service
import org.springframework.web.socket.CloseStatus
import org.springframework.web.socket.TextMessage
import org.springframework.web.socket.WebSocketSession
import org.springframework.web.socket.client.standard.StandardWebSocketClient
import org.springframework.web.socket.handler.TextWebSocketHandler
import java.io.IOException
import java.util.concurrent.ConcurrentHashMap

//https://github.com/spring-projects/spring-framework/blob/main/spring-websocket/src/main/java/org/springframework/web/socket/messaging/SubProtocolWebSocketHandler.java

@Service
class WebsocketHandler(val applicationEventPublisher: ApplicationEventPublisher) : TextWebSocketHandler() {

    private val mapper = jacksonObjectMapper()
        .setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY)
        .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)

    private var websocketServer: String = ""

    private val standardWebSocketClient: StandardWebSocketClient = StandardWebSocketClient()
    private var clientSession: WebSocketSession? = null

    private var websocketSession: Int = 0
    private var sessions: MutableMap<String, WebsocketSessionHolder> = ConcurrentHashMap()

    private val log by logger()

    fun connect(websocketServer: String): Boolean {
        this.websocketServer = websocketServer
        log.info("Websocket connect request")
        try {
            clientSession?.close()

            clientSession = standardWebSocketClient
                .execute(
                    this,
                    this.websocketServer
                ) //        .doHandshake(this, new WebSocketHttpHeaders(), URI.create(this.websocketServer))
                .get()

            sessions[clientSession!!.id] = WebsocketSessionHolder(clientSession)
            log.info("Adding Websocket session: id=${clientSession!!.id}")

            return true
        } catch (e: Exception) {
            log.error("Websocket connect failed!")
        }
        return false
    }

    override fun handleTextMessage(session: WebSocketSession, message: TextMessage) {
        try {
            val msg = message.payload
            log.debug("handleTextMessage: $msg")
            val m: WebsocketMessage = mapper.readValue(msg)
            m.d=msg.substring(msg.indexOf("{", 1), msg.length - 46)
            applicationEventPublisher.publishEvent(WebsocketMessageEvent(m))
        } catch (e: JsonProcessingException) {
            // TODO Auto-generated catch block
            log.error(message.payload)
        } catch (e: Exception) {
            // TODO Auto-generated catch block
            log.error(message.payload)
        }
    }

    fun sendMsg(message: String) {
        log.debug("Websocket sendMsg: $message")
        try {

            val holder = sessions[clientSession!!.id]
            if (holder == null) {
                log.debug("No Websocket session")
                return
            }
            holder.getSession()?.sendMessage(TextMessage(message)) ?: error("Websocket sendMsg failed!")
        } catch (e: IOException) {
            // TODO Auto-generated catch block
            log.error("Websocket sendMsg failed: ${e.message}")
        }

        //TODO: The remote endpoint was in state [TEXT_PARTIAL_WRITING] which is an invalid state for called method
    }

    fun disconnect() {
        log.info("Websocket disconnect")
        try {
            clientSession?.id?.let {
                sessions.remove(it)?.let { session ->
                    log.info("Removing Websocket session: $session")
                }
            }
            clientSession?.close()
        } catch (e: IOException) {
            // TODO Auto-generated catch block
            log.error("Websocket disconnect failed!")
        }
        websocketSession = 0
    }

    /*
    @SneakyThrows
    public static void main(String[] args)  {
        WebsocketController pureClient =  new WebsocketController();
        Thread.sleep(1000);
        pureClient.getClientSession().sendMessage(new TextMessage("{\"action\":\"authentication\",\"loginId\":\"demokvJbJg5447\",\"tradingAccountType\":\"SPREAD\",\"token\":\"R126Uw39cgBdwaDN0VHW9MCWNBPP9G1l9NFCc05yABKDPM02N3fYq49K7vLfjFImAkIjKyv1DUWhjbr4\"}"));
        Thread.sleep(1000);
        pureClient.getClientSession().sendMessage(new TextMessage("{\"quoteId\":6374,\"priceGrouping\":\"Sampled\",\"action\":\"subscribe\"}"));
        Thread.sleep(60000);
        pureClient.getClientSession().close();
    }
*/
    override fun afterConnectionEstablished(session: WebSocketSession) {
        log.info("Websocket ConnectionEstablished")
        websocketSession = 1
    }

    override fun afterConnectionClosed(session: WebSocketSession, status: CloseStatus) {
        log.info("Websocket ConnectionClosed")
        applicationEventPublisher.publishEvent(WebsocketDisconnectedEvent())
        websocketSession = 0
    }

}
