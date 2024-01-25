package com.piotr.marketbroker.infrastructure.websocket

import com.fasterxml.jackson.annotation.JsonAutoDetect
import com.fasterxml.jackson.annotation.PropertyAccessor
import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.JsonMappingException
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.piotr.marketbroker.application.event.WebsocketDisconnectedEvent
import com.piotr.marketbroker.application.event.WebsocketMessageEvent
import mu.KotlinLogging
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Service
import org.springframework.web.socket.CloseStatus
import org.springframework.web.socket.TextMessage
import org.springframework.web.socket.WebSocketSession
import org.springframework.web.socket.client.standard.StandardWebSocketClient
import org.springframework.web.socket.handler.TextWebSocketHandler
import java.io.IOException

private val log = KotlinLogging.logger {}

@Service
class WebsocketSessionHandler(val applicationEventPublisher: ApplicationEventPublisher) : TextWebSocketHandler() {
    private val mapper = jacksonObjectMapper()
        .setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY)
        .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)

    private var websocketServer: String = ""

    private val standardWebSocketClient: StandardWebSocketClient = StandardWebSocketClient()
    private var clientSession: WebSocketSession? = null

    private var websocketSession: Int = 0


    fun connect(websocketServer: String): Boolean {
        this.websocketServer = websocketServer
        log.info("Websocket connect request")
        try {
            clientSession?.close()

            this.clientSession = standardWebSocketClient
                .execute(
                    this,
                    this.websocketServer
                ) //        .doHandshake(this, new WebSocketHttpHeaders(), URI.create(this.websocketServer))
                .get()
            return true
        } catch (e: Exception) {
            log.error("Websocket connect failed!")
        }
        return false
    }

    @Throws(IOException::class)
    override fun handleTextMessage(session: WebSocketSession, message: TextMessage) {
        try {
            val msg = message.payload
            val m: WebsocketDTO = mapper.readValue(msg)
            m.d=msg.substring(msg.indexOf("{", 1), msg.length - 46)
            log.info(m.d)

            /*
            JsonNode jsonNode = mapper.readTree(msg);
            JsonParser jsonParser = jsonNode.traverse();
            while (!jsonParser.isClosed()) {
                JsonToken token = jsonParser.nextToken();
                if (token == JsonToken.FIELD_NAME) {
                    log.info(jsonParser.getCurrentName());

                } else if (token == JsonToken.VALUE_STRING) {
                    log.info(jsonParser.getValueAsString());

                }
            }
*/
            applicationEventPublisher.publishEvent(WebsocketMessageEvent(m))
        } catch (e: JsonProcessingException) {
            // TODO Auto-generated catch block
            log.error(message.toString())
        } catch (e: JsonMappingException) {
            // TODO Auto-generated catch block
            log.error(message.toString())
        }
    }

    fun sendMsg(message: String) {
        log.info("Websocket sendMsg: $message")
        try {
            clientSession!!.sendMessage(TextMessage(message))
        } catch (e: IOException) {
            // TODO Auto-generated catch block
            log.error("Websocket sendMsg failed!")
            log.error(message)
        }
    }

    fun disconnect() {
        log.info("Websocket disconnect")
        try {
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