package com.piotr.marketbroker.application.service

import ai.symmetrical.kafka.producer.VariableTopicMessageProducer
import com.piotr.marketbroker.application.event.kafka.transaction.TransactionEvent
import com.piotr.marketbroker.application.event.kafka.transaction.TransactionType
import com.piotr.marketbroker.common.logger
import com.piotr.marketbroker.configuration.kafka.KafkaTopics.TOPIC_TRANSACTIONS

import com.fasterxml.jackson.annotation.JsonAutoDetect
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.PropertyAccessor
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.piotr.marketbroker.application.handler.AccountDetailsHandler
import com.piotr.marketbroker.infrastructure.http.ApacheHttpAdapter
import com.piotr.marketbroker.infrastructure.http.HttpAdapterResponse
import com.piotr.marketbroker.infrastructure.http.RequestHeaders
import com.piotr.marketbroker.domain.order.OpenOrderResponse
import com.piotr.marketbroker.domain.order.Order
import com.piotr.marketbroker.domain.order.TradeRequestResponse
import com.piotr.marketbroker.common.logger
import com.piotr.marketbroker.domain.order.TransactionHistoryOrder
import com.piotr.marketbroker.domain.order.TransactionHistoryOrders
import com.piotr.marketbroker.domain.order.port.OrdersRepository
import org.springframework.stereotype.Service
import java.time.LocalDateTime
import java.time.OffsetDateTime
import java.time.ZoneOffset
import kotlin.math.max
import kotlin.math.min
import kotlin.math.round

private const val INSERT_OPEN_ORDER = "InsertOpenOrder"
private const val GET_OPEN_ORDER = "GetOpenOrder"
private const val DELETE_ORDER = "DeleteOrder"
private const val REQUEST_TRADE = "RequestTrade"
private const val INSERT_CLOSE_POSITION = "InsertClosePosition"
private const val GET_TRANSACTION_HISTORY = "GetTransactionHistory"

private const val ORDER_QUERY = "{\"orderID\":%d}"
private const val HISTORY_QUERY = "{\"transType\":2,\"days\":3,\"page\":0}"


@Service
class OrdersService(
    private val httpAdapter: ApacheHttpAdapter,
    private val ordersRepository: OrdersRepository,
    private val accountDetailsHandler: AccountDetailsHandler,
    private val producer: VariableTopicMessageProducer<TransactionEvent>
) {
    private val log by logger()

    private val mapper = jacksonObjectMapper()
        .registerModule(JavaTimeModule())
        .setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY)
        .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)

    fun insertClosePosition(order: Order): TradeRequestResponse {

        var insertClosePositionRequestDTO = InsertClosePositionRequestDTO(0, 0, 0, "", 0f, false, key = order.key!!)
        var orderId: Int = 0

        if (order.orderModeID==5) {
            // return TradeRequestResponse(message = "InsertClosePosition error: Order mode 5 is not supported", status = -1)
            val orderToClose = ordersRepository.findByOrderId(order.positionId)
            if (orderToClose==null) {
                val msg = "InsertClosePosition error: Order id=${order.positionId} does not exist"
                log.warn(msg)
                return TradeRequestResponse(message = msg, status = -1)
            }
            insertClosePositionRequestDTO = InsertClosePositionRequestDTO(
                order.marketId,
                orderToClose.positionId,
                order.quoteId,
                order.price.toString(),
                orderToClose.stake,
                // order.direction == -1,
                orderToClose.direction == 1,
                order.key
            )
            orderId = orderToClose.orderId
        }


        if (order.orderModeID==4) {
            val position = accountDetailsHandler.getPositionByPositionId(order.positionId)
            if (position==null) {
    //        if (!accountDetailsHandler.positionExists(order.positionId)) {
                val msg = "InsertClosePosition error: Position ${order.positionId} does not exist"
                log.warn(msg)
                return TradeRequestResponse(message = msg, status = -1)
            }

            if (position.stake != order.stake) {          //for now only full close is supported in orderMode=4
                val msg =
                    "InsertClosePosition error: You are only allowed to close full position (size=${position.stake}) id=${order.positionId}"
                log.warn(msg)
                return TradeRequestResponse(message = msg, status = -1)
            }
            // if (when (position.direction) {
            //         "Buy" -> order.direction == 1
            //         "Sell" -> order.direction == -1
            //         else -> true
            //     }
            // ) {
            //     val msg =
            //         "InsertClosePosition error: You must close in opposite direction to the position id=${order.positionId}"
            //     log.warn(msg)
            //     return TradeRequestResponse(message = msg, status = -1)
            // }

            if (order.marketId != position.marketId || order.quoteId != position.quoteId) {
                val msg =
                    "InsertClosePosition error: Position ${order.positionId} does not belong to market ${order.marketId} quote ${order.quoteId}"
                log.warn(msg)
                return TradeRequestResponse(message = msg, status = -1)
            }

            insertClosePositionRequestDTO = InsertClosePositionRequestDTO(
                order.marketId,
                order.positionId,
                order.quoteId,
                order.price.toString(),
                order.stake,
                // order.direction == -1,
                position.direction == "Buy",
                order.key
            )
            orderId = position.orderId
        }

        log.info("insertClosePosition: orderId=$orderId")

        val httpResponse: HttpAdapterResponse
        try {
            val jsonString = mapper.writeValueAsString(insertClosePositionRequestDTO)
            log.info("insertClosePosition: $jsonString")
            httpResponse = httpAdapter.postRequest(INSERT_CLOSE_POSITION, jsonString, RequestHeaders.postHeaders)
        } catch (e: Exception) {
            val msg = "InsertClosePosition exception: ${e.message}"
            log.error(msg)
            return TradeRequestResponse(message = msg, status = -1)
        }
        if (httpResponse.statusCode != 200) {
            log.error("insertClosePosition error: ${httpResponse.statusCode} ${httpResponse.body}")
            return TradeRequestResponse(message = httpResponse.body, status = -1)
        }

        log.info("insertClosePosition response body: ${httpResponse.body}")
        try {
            val response = httpResponse.body.substring(5, httpResponse.body.length - 1)
            val tradeRequestResponse: TradeRequestResponse = mapper.readValue(response)

            // val tradeRequestResponse = saveTradeRequest(httpResponse, order)

            val orderToClose = ordersRepository.findByOrderId(orderId)
            log.info("insertClosePosition: orderToClose=$orderToClose")

            if (orderToClose==null) {
                val msg = "InsertClosePosition error: Order id=${orderId} could not be found, yet the request was successful"
                log.warn(msg)
                return TradeRequestResponse(message = msg, status = -1)
            }
            orderToClose.active = false
            orderToClose.updatedAt = LocalDateTime.now()
            orderToClose.close_price = tradeRequestResponse.price
            orderToClose.close_date = OffsetDateTime.now(ZoneOffset.UTC).toLocalDateTime()
            orderToClose.updatedAt = LocalDateTime.now()
            ordersRepository.save(orderToClose)


            producer.produce(
                TransactionEvent(
                    o = tradeRequestResponse.orderId.toInt(),
                    p = tradeRequestResponse.positionId,
                    type = TransactionType.CLOSED,
                    price = tradeRequestResponse.price,
                    t = orderToClose.close_date?.toEpochSecond(ZoneOffset.UTC)?:0L
                ),
                TOPIC_TRANSACTIONS, null)

            return tradeRequestResponse
        } catch (e: Exception) {
            val msg = "To TradeRequestResponse mapping exception: ${e.toString()}"
            log.error(msg)
            return TradeRequestResponse(message = msg, status = -1)
        }

    }

    fun requestTrade(order: Order): TradeRequestResponse {                               //Market order
        val limit: Boolean = order.limitOrderPrice > 0
//        val stop: Boolean = order.stopOrderPrice > 0

        if (order.stopOrderPrice>0) {
            if ((order.direction==1) && (order.stopOrderPrice>=order.price)) {
                return TradeRequestResponse(message = "Stop order price is greater than or equal to price", status = -1)
            }
            if ((order.direction==-1) && (order.stopOrderPrice<=order.price)) {
                return TradeRequestResponse(message = "Stop order price is less than or equal to price", status = -1)
            }
        }

        val onePercentSl = if (order.direction==1)
            max(order.price*0.99f, order.stopOrderPrice)
        else
            min(order.price*1.01f, if (order.stopOrderPrice>0) order.stopOrderPrice else 1000000f)
        
        val stop = true

        val requestTradeDTO = RequestTradeDTO(
            order.marketId,
            order.quoteId,
            order.price,
            order.stake.toString(),
            //        .tradeType(1)                                       //1
            order.direction == -1,
            limit || stop,
            (if (limit) 1 else 0) + (if (stop) 2 else 0),
            //        .orderTypeID(2)                                 //2

            if (limit || stop) 2 else 0,
            round(onePercentSl*10000)/10000f,               //order.stopOrderPrice,
            order.limitOrderPrice,
            if (order.trailingPoint) 1 else 0,
            //        .closePositionID(0)
            //        .isKaazingFeed(true)
            //        .userAgent("Firefox (103.0)")
            order.key!!
        )

        var httpResponse: HttpAdapterResponse = HttpAdapterResponse(0, "")
        try {
            val jsonString = mapper.writeValueAsString(requestTradeDTO)
            log.info("requestTrade: $jsonString")
            httpResponse = httpAdapter.postRequest(REQUEST_TRADE, jsonString, RequestHeaders.postHeaders)
        } catch (e: Exception) {
            val msg = "requestTrade exception: ${e.message}"
            log.error(msg)
            return TradeRequestResponse(message = httpResponse.body, status = -1)
        }
        if (httpResponse.statusCode != 200) {
            log.error("requestTrade error: ${httpResponse.statusCode} ${httpResponse.body}")
            return TradeRequestResponse(message = httpResponse.body, status = -1)
        }
        log.info("requestTrade response body: ${httpResponse.body}")
        try {
            // val response = httpResponse.body.substring(5, httpResponse.body.length - 1)
            // val tradeRequestResponse: TradeRequestResponse = mapper.readValue(response)

            val tradeRequestResponse = saveTradeRequest(httpResponse, order)

            producer.produce(
                TransactionEvent(
                    o = tradeRequestResponse.orderId.toInt(),
                    p = tradeRequestResponse.positionId,
                    type = TransactionType.FILLED,
                    price = tradeRequestResponse.price,
                    sl = tradeRequestResponse.stopOrderPrice.toFloat(),
                    tp = tradeRequestResponse.limitOrderPrice.toFloat(),
                    t = order.createdAt.toEpochSecond(ZoneOffset.UTC)
                ),
                TOPIC_TRANSACTIONS, null)

            return tradeRequestResponse
        } catch (e: Exception) {
            val msg = "To TradeRequestResponse mapping exception: ${e.message}"
            log.error(msg)
            return TradeRequestResponse(message = msg, status = -1)
        }
//        producer.produce(                                           // Alternative way to throw market order filled event, currently implemented based on AccountDetailsEvent websocket event
//            TransactionEvent(
//                o = order.orderId,
//                p = order.positionId,
//                type = TransactionType.FILLED,
//                price = order.open_price,
//                sl = order.stopOrderPrice,
//                tp = order.limitOrderPrice,
//                t = order.open_date?.toEpochSecond(ZoneOffset.UTC)?:0L
//            ),
//            TOPIC_TRANSACTIONS, null)
    }

/*
    private void GetOpenOrderDetails(int orderId) {
        String query = String.format(GET_OPEN_ORDER_QUERY, orderId);
        HttpResponseDto httpResponseDto = tdHttpClient.postRequest(GET_OPEN_ORDER, query);
    }
*/
    fun insertOpenOrder(order: Order): OpenOrderResponse {                      // Open order or Open stop order
        val limit: Boolean = order.limitOrderPrice > 0
        val stop: Boolean = order.stopOrderPrice > 0

        val insertOpenOrderRequestDTO = InsertOpenOrderRequestDTO(
            marketID = order.marketId,
            marketQuoteID = order.quoteId,
            tradeModeID = order.direction == -1,
            orderStake = order.stake.toString(),
            orderModeID = order.orderModeID,
            limitOrderPrice = if (order.orderModeID==1) order.price else 0f,
            stopOrderPrice = if (order.orderModeID==2) order.price else 0f,
            hasIfDoneOrder = limit || stop,
            iDOOrderModeID = (if (limit) 1 else 0) + (if (stop) 2 else 0),
            iDOLimitOrderPrice = order.limitOrderPrice.toString(),
            iDOStopOrderPrice = order.stopOrderPrice.toString(),
            trailingPoint = if (order.trailingPoint) 1 else 0,
        )

        var httpResponse: HttpAdapterResponse = HttpAdapterResponse(0, "")
        try {
            val jsonString = mapper.writeValueAsString(insertOpenOrderRequestDTO)
            log.info("insertOpenOrder request: $jsonString")
            httpResponse = httpAdapter.postRequest(INSERT_OPEN_ORDER, jsonString, RequestHeaders.postHeaders)
            log.info("insertOpenOrder response: ${httpResponse.statusCode} ${httpResponse.body}")
        } catch (e: Exception) {
            val msg = "insertOpenOrder exception: ${e.message}. HTTP status: ${httpResponse.statusCode} ${httpResponse.body}"
            // TODO Auto-generated catch block
            log.error(msg)
            // TODO Auto-generated catch block
            return OpenOrderResponse(message = httpResponse.body, status = -1)
        }

        if (httpResponse.statusCode != 200) {
            log.error("insertOpenOrder error: ${httpResponse.statusCode} ${httpResponse.body}")
            return OpenOrderResponse(message = httpResponse.body, status = -1)
        }

        log.info("insertOpenOrder response body: ${httpResponse.body}")
        try {
            val response = httpResponse.body.substring(5, httpResponse.body.length - 1)
            val openOrderResponse: OpenOrderResponse = mapper.readValue(response)
            if (openOrderResponse.status!=0) return openOrderResponse
            order.orderId = openOrderResponse.orderId.toInt()
            order.openOrderResponse = openOrderResponse
            ordersRepository.save(order)

            producer.produce(
                TransactionEvent(
                    o = order.orderId,
                    type = TransactionType.PENDING,
                    price = order.price,
                    sl = order.stopOrderPrice,
                    tp = order.limitOrderPrice,
                    t = order.createdAt.toEpochSecond(ZoneOffset.UTC)
                ),
                TOPIC_TRANSACTIONS, null)

            return openOrderResponse
        } catch (e: Exception) {
            val msg = "To OpenOrderResponse mapping exception: ${e.message}"
            log.error(msg)
            return OpenOrderResponse(message = msg, status = -1)
        }
    }

    fun deleteOrder(orderId: Int): Boolean {
        try {
            log.info("DeleteOrder request: $orderId")
            if (accountDetailsHandler.getOpeningOrderByOrderId(orderId)?.active != true) {
                log.warn("DeleteOrder: Order $orderId not found or inactive")
                return false
            }
            val httpResponse =
                httpAdapter.postRequest(
                    DELETE_ORDER,
                    String.format(ORDER_QUERY, orderId),
                    RequestHeaders.postHeaders
                )
            if (httpResponse.statusCode == 200) {
                val datetimenow = LocalDateTime.now()
                val order = ordersRepository.findByOrderId(orderId)
                if (order!=null) {
                    order.active = false
                    order.updatedAt = datetimenow
                    ordersRepository.save(order)
                }

                producer.produce(
                    TransactionEvent(
                        o = orderId,
                        p = order?.positionId,
                        type = TransactionType.CANCELLED,
                        price = order?.price?:0f,
                        sl = order?.stopOrderPrice?:0f,
                        tp = order?.limitOrderPrice?:0f,
                        t = datetimenow.toEpochSecond(ZoneOffset.UTC)
                    ),
                    TOPIC_TRANSACTIONS, null)   

                return true
            }
            log.warn("DeleteOrder: Order $orderId could not be deleted")
            return false
        } catch (e: Exception) {
            log.error("DeleteOrder error: ${String.format(ORDER_QUERY, orderId)}")
            return false
        }
    }

    fun getOrder(id: Int): OpenOrderResponse? {
        var httpResponse: HttpAdapterResponse = HttpAdapterResponse(0, "")
        try {
            log.info("GetOrder request: $id")
            httpResponse =
                httpAdapter.postRequest(GET_OPEN_ORDER, String.format(ORDER_QUERY, id), RequestHeaders.postHeaders)
            if (httpResponse.statusCode != 200) {
                log.error("getOrder error: ${httpResponse.statusCode} ${httpResponse.body}")
                return OpenOrderResponse(message = httpResponse.body, status = -1)
            }
            log.info("getOrder response body: ${httpResponse.body}")
            val response = httpResponse.body.substring(5, httpResponse.body.length - 1)
            // Possible response: {"__type":"TradingPlatform.OpenOrder","OrderID":null,"QuoteID":null,"MarketID":null,"Market":null,"ExpiryDate":null,"TradeMode":null,"Stake":null,"OrderMode":null,"OrderType":null,"OrderPriceMode":null,"LimitOrderPrice":null,"StopOrderPrice":null,"OrderStatus":null,"IsForceOpen":false,"IDOID":null,"IDOOrderMode":null,"IDOTradeMode":null,"IDOIsGuaranteedStop":false,"IDOLimitOrderPrice":null,"IDOStopOrderPrice":null,"IDOTrailingPoint":null,"Currency":null,"TrailingPoint":null,"IsRollingMarket":false,"Status":-996,"Message":"Session Expired."}
            try {
                val openOrderResponse: OpenOrderResponse = mapper.readValue(response)
                return openOrderResponse
            } catch (e: Exception) {
                log.error("To OpenOrderResponse mapping exception: ${e.message}")
                log.debug("getOrder response: $response")
                return OpenOrderResponse(message = e.message, status = -1)
            }
        } catch (e: Exception) {
            log.error("GetOrder exception: ${e.message}")
            return OpenOrderResponse(message = e.message, status = -1)
        }
    }

    fun getOrders(): List<Order> {
        return ordersRepository.findAll()
    }

    fun getHistory(): List<TransactionHistoryOrder>? {
        var httpResponse: HttpAdapterResponse
        try {
            log.info("GetHistory request")
            httpResponse =
                httpAdapter.postRequest(GET_TRANSACTION_HISTORY, HISTORY_QUERY, RequestHeaders.postHeaders)
            if (httpResponse.statusCode != 200) {
                log.error("getHistory error: ${httpResponse.statusCode} ${httpResponse.body}")
                return listOf()
            }
            log.info("getHistory response body: ${httpResponse.body}")
            if (httpResponse.body.length < 20) {
                log.warn("getHistory response body empty")
                return listOf()
            }
            try {
                val response: String = httpResponse.body.substring(10, httpResponse.body.length - 2)
                val transactionHistoryResponse: TransactionHistoryOrders = mapper.readValue(response)
                return transactionHistoryResponse.records
            } catch (e: Exception) {
                log.error("To TransactionHistoryOrders mapping exception: ${e.message}")
                return listOf()
            }
        } catch (e: Exception) {
            log.error("getHistory exception: ${e.message}")
            return listOf()
        }
    }

    private fun saveTradeRequest(httpResponse: HttpAdapterResponse, order: Order): TradeRequestResponse {
        log.info("Saving trade request response")
        val response = httpResponse.body.substring(5, httpResponse.body.length - 1)
        val tradeRequestResponse: TradeRequestResponse = mapper.readValue(response)
        order.orderId = tradeRequestResponse.orderId.toInt()
        order.tradeRequestResponse = tradeRequestResponse
        order.open_price = tradeRequestResponse.price
        order.open_date = OffsetDateTime.now(ZoneOffset.UTC).toLocalDateTime()
        order.positionId = tradeRequestResponse.positionId
        ordersRepository.save(order)
        return tradeRequestResponse
    }

}


private const val userAgentValue = "Safari/537.36"

internal class InsertClosePositionRequestDTO (
    private val marketID: Int,
    private val positionID: Int,
    private val quoteID: Int,
    private val price: String,
    private val stake: Float,
    private val tradeMode: Boolean,
    @JsonProperty("isKaazingFeed")
    private val isKaazingFeed: Boolean = true,
    private val userAgent: String = userAgentValue,
    private val key: String
) {
    internal constructor(m: Int, p: Int, q: Int, price: String, s: Float, t: Boolean, k: String)
            : this (m, p, q, price, s, t, true, userAgentValue, k)
}

internal class RequestTradeDTO (
    private val marketID: Int,
    private val quoteID: Int,
    private val price: Float,
    private val stake: String,
    private val tradeType: Int = 1,
    private val tradeMode: Boolean,
    private val hasClosingOrder: Boolean,
    @JsonProperty("isGuaranteed")
    private val isGuaranteed: Boolean = false,
    private val orderModeID: Int,
    private val orderTypeID: Int = 2,
    private val orderPriceModeID: Int,
    private val stopOrderPrice: Float,
    private val limitOrderPrice: Float,
    private val trailingPoint: Int,
    private val closePositionID: Int = 0,
    @JsonProperty("isKaazingFeed")
    private val isKaazingFeed: Boolean = true,
    private val userAgent: String = userAgentValue,
    private val key: String
) {
    internal constructor(
        marketID: Int,
        quoteID: Int,
        price: Float,
        stake: String,
        tradeMode: Boolean,
        hasClosingOrder: Boolean,
        orderModeID: Int,
        orderPriceModeID: Int,
        stopOrderPrice: Float,
        limitOrderPrice: Float,
        trailingPoint: Int,
        key: String
        ) : this(marketID, quoteID, price, stake, 1, tradeMode, hasClosingOrder, false,
                orderModeID, 2, orderPriceModeID, stopOrderPrice, limitOrderPrice,
                trailingPoint, 0, true, userAgentValue, key)
}

internal class InsertOpenOrderRequestDTO(
    private val tradeType: Int = 1,
    private val marketID: Int,
    private val marketQuoteID: Int,
    private val tradeModeID: Boolean,
    private val orderStake: String,
    private val orderModeID: Int,
    private val orderTypeID: Int = 2,
    private val orderPriceModeID: Int = 2,
    private val limitOrderPrice: Float,
    private val stopOrderPrice: Float,
    private val hasIfDoneOrder: Boolean,
    private val trailingPoint: Int = 0,

    @JsonProperty("IDOIsGuarantee")
    private val iDOIsGuarantee: Boolean = false,

    @JsonProperty("IDOOrderModeID")
    private val iDOOrderModeID: Int,

    @JsonProperty("IDOLimitOrderPrice")
    private val iDOLimitOrderPrice: String,

    @JsonProperty("IDOStopOrderPrice")
    private val iDOStopOrderPrice: String
) {
    internal constructor(
        marketID: Int,
        marketQuoteID: Int,
        tradeModeID: Boolean,
        orderStake: String,
        orderModeID: Int,
        limitOrderPrice: Float,
        stopOrderPrice: Float,
        hasIfDoneOrder: Boolean,
        iDOOrderModeID: Int,
        iDOLimitOrderPrice: String,
        iDOStopOrderPrice: String
    ) : this(1, marketID, marketQuoteID, tradeModeID, orderStake, orderModeID, 2, 2,
        limitOrderPrice, stopOrderPrice, hasIfDoneOrder,
        0, false, iDOOrderModeID, iDOLimitOrderPrice, iDOStopOrderPrice)
}

internal class AmendOpenOrderRequestDTO(

    @JsonProperty("orderID")
    private val orderId: Int,

    @JsonProperty("orderStake")
    private val orderStake: String,

    @JsonProperty("orderModeId")                        // 1 - Open limit order, 2 - Open stop limit order
    private val orderModeID: Int,

    @JsonProperty("orderTypeId")
    private val orderTypeID: Int = 2,

    @JsonProperty("orderPriceModeId")
    private val orderPriceModeID: Int = 2,

    @JsonProperty("limitOrderPrice")
    private val limitOrderPrice: Float,

    @JsonProperty("stopOrderPrice")
    private val stopOrderPrice: Float,

    @JsonProperty("iDOAction")
    private val iDOAction: Int,

    @JsonProperty("IDOIsGuarantee")
    private val iDOIsGuarantee: Boolean = false,

    @JsonProperty("IDOOrderModeID")
    private val iDOOrderModeID: Int,

    @JsonProperty("IDOLimitOrderPrice")
    private val iDOLimitOrderPrice: String,

    @JsonProperty("IDOStopOrderPrice")
    private val iDOStopOrderPrice: String
) {
    internal constructor(
        orderID: Int,
        orderStake: String,
        orderModeID: Int,
        limitOrderPrice: Float,
        stopOrderPrice: Float,
        iDOAction: Int,
        iDOOrderModeID: Int,
        iDOLimitOrderPrice: String,
        iDOStopOrderPrice: String
    ) : this(orderID, orderStake, orderModeID, 2, 2, limitOrderPrice, stopOrderPrice,
        iDOAction, false, iDOOrderModeID, iDOLimitOrderPrice, iDOStopOrderPrice)
}
