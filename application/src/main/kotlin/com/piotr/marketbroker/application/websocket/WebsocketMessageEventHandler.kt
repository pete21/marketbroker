package com.piotr.marketbroker.application.websocket

import com.fasterxml.jackson.annotation.JsonAutoDetect
import com.fasterxml.jackson.annotation.PropertyAccessor
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.piotr.marketbroker.domain.accountdetails.event.AccountDetailsEvent
import com.piotr.marketbroker.domain.accountsummary.event.AccountSummaryEvent
import com.piotr.marketbroker.domain.subscription.event.SubscriptionEvent
import com.piotr.marketbroker.domain.tick.event.TickData
import com.piotr.marketbroker.domain.tick.event.TicksEvent
import com.piotr.marketbroker.application.event.WebsocketMessageEvent
import com.piotr.marketbroker.domain.accountdetails.AccountDetailsDto
import com.piotr.marketbroker.domain.accountsummary.AccountSummaryDto
import com.piotr.marketbroker.application.websocket.message.WebsocketTicks
import com.piotr.marketbroker.application.websocket.message.WebsocketSubscribeResponse
import com.piotr.marketbroker.infrastructure.websocket.WebsocketHandler
import com.piotr.marketbroker.common.logger
import org.springframework.context.ApplicationEventPublisher
import org.springframework.context.event.EventListener
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Service
import java.time.Instant
import java.time.temporal.ChronoUnit


@Service
class WebsocketMessageEventHandler(
    private val websocketHandler: WebsocketHandler,
    private val applicationEventPublisher: ApplicationEventPublisher
) {

    private val log by logger()

    private val stringBuilder = StringBuilder(2000)
    private val mapper = jacksonObjectMapper()
        .registerModule(JavaTimeModule())
        .setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY)
        .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)

    private val RECEIVED_BY_CLIENT = ",\"ReceivedByClient\":\""
    private val SENT_BY_CLIENT = "\",\"SentByClient\":\""
    private val HEARTBEAT = "\",\"action\":\"heartbeat\"}"


    @Async
    @EventListener
    fun handleMessage(event: WebsocketMessageEvent) {
        val msg = event.message
        when (msg.t) {
            "heartbeat" -> {
                val timestamp = Instant.now().truncatedTo(ChronoUnit.MILLIS)
                stringBuilder.setLength(0)
                stringBuilder.append(msg.d)
                stringBuilder.setLength(stringBuilder.length - 1)
                stringBuilder.append(RECEIVED_BY_CLIENT)
                stringBuilder.append(timestamp)
                stringBuilder.append(SENT_BY_CLIENT)
                stringBuilder.append(timestamp)
                stringBuilder.append(HEARTBEAT)
                websocketHandler.sendMsg(stringBuilder.toString())
            }

            "connectResponse" -> log.info(msg.t + ": " + msg.d)

            "authenticationResponse" -> log.info(msg.t + ": " + msg.d)

            "subscribeResponse", "unsubscribeResponse" -> try {
                val m: WebsocketSubscribeResponse = mapper.readValue(msg.d!!)
                log.info("${msg.t} : $m")
                applicationEventPublisher.publishEvent(SubscriptionEvent(m.quoteId, m.action, m.result))
            } catch (e: Exception) {
                log.error(e.toString())
                log.error(msg.t + ": " + msg.d)
            }

            "p" -> try {
                val m: WebsocketTicks = mapper.readValue(msg.d!!)

                val tickList: List<TickData> = m.sp.map { s ->
                    val values = s.split(",")
                    TickData(
                        values[0].toInt(),
                        values[1].toFloat(),
                        values[2].toFloat(),
                        values[10].toFloat(),
                        values[11].toLong(),
                        values[8]
                    )
                }
                applicationEventPublisher.publishEvent(TicksEvent(tickList))
            } catch (e: Exception) {
                log.error(e.toString())
                log.error(msg.t + ": " + msg.d)
            }

            "accountSummary" -> try {
                val m: AccountSummaryDto = mapper.readValue(msg.d!!)
                log.info("${msg.t} : $m")
                applicationEventPublisher.publishEvent(AccountSummaryEvent(m))
            } catch (e: Exception) {
                log.error(e.toString())
                log.error(msg.t + ": " + msg.d)
            }

            "accountDetails" -> try {
                val m: AccountDetailsDto = mapper.readValue(msg.d!!)

                if (m.positions!=null && m.openingOrders!=null) {
                    if (m.positions.totalRecords > 0 || m.openingOrders.totalRecords > 0) {
                        applicationEventPublisher.publishEvent(
                            AccountDetailsEvent(m.positions.records, m.openingOrders.records)
                        )
                    }
                    log.info("Positions: ${m.positions.totalRecords} ${m.positions.records}")
                    log.info("OpeningOrders: ${m.openingOrders.totalRecords} ${m.openingOrders.records}")
                }
            } catch (e: Exception) {
                log.error(e.toString())
                log.error(msg.t + ": " + msg.d)
            }

            "optionsResponse" ->{log.info(msg.t + ": " + msg.d)}
            else -> {log.info(msg.t + ": " + msg.d)}
        }
    }

}
