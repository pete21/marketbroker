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
private const val HISTORY_QUERY = "{\"transType\":2,\"days\":1,\"page\":0}"


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

        if (order.orderModeID==5) {
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
                order.direction == -1,
                order.key
            )
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
            if (when (position.direction) {
                    "Buy" -> order.direction == 1
                    "Sell" -> order.direction == -1
                    else -> true
                }
            ) {
                val msg =
                    "InsertClosePosition error: You are only allowed to close in opposite direction id=${order.positionId}"
                log.warn(msg)
                return TradeRequestResponse(message = msg, status = -1)
            }

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
                order.direction == -1,
                order.key
            )
        }

        val httpResponse: HttpAdapterResponse
        try {
            val jsonString = mapper.writeValueAsString(insertClosePositionRequestDTO)
            log.info("insertClosePosition: $jsonString")
            httpResponse = httpAdapter.postRequest(INSERT_CLOSE_POSITION, jsonString, RequestHeaders.postHeaders)
        } catch (e: Exception) {
            val msg = "InsertClosePosition error: ${e.message}"
            log.error(msg)
            return TradeRequestResponse(message = msg, status = -1)
        }
//        val tradeRequst = saveTradeRequest(httpResponse, order)
//        return tradeRequst
        val response = httpResponse.body.substring(5, httpResponse.body.length - 1)
        try {
            val tradeRequestResponse: TradeRequestResponse = mapper.readValue(response)
            return tradeRequestResponse
        } catch (e: Exception) {
            val msg = "TradeRequest mapping error: ${e.message}"
            log.error(msg)
            log.debug("TradeRequest json: $response")
            // TODO Auto-generated catch block
            return TradeRequestResponse(message = msg, status = -1)
        }

    }

    fun requestTrade(order: Order): TradeRequestResponse {                               //Market order
        val limit: Boolean = order.limitOrderPrice > 0
//        val stop: Boolean = order.stopOrderPrice > 0

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

        val httpResponse: HttpAdapterResponse
        try {
            val jsonString = mapper.writeValueAsString(requestTradeDTO)
            log.info("requestTrade: $jsonString")
            httpResponse = httpAdapter.postRequest(REQUEST_TRADE, jsonString, RequestHeaders.postHeaders)
        } catch (e: Exception) {
            val msg = "requestTrade error: ${e.message}"
            log.error(msg)
            // TODO Auto-generated catch block
            return TradeRequestResponse(message = msg, status = -1)
        }
        val tradeRequest = saveTradeRequest(httpResponse, order)
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
        return tradeRequest
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
        } catch (e: Exception) {
            val msg = "RequestTrade error: ${e.message}. HTTP status: ${httpResponse.statusCode} ${httpResponse.body}"
            // TODO Auto-generated catch block
            log.error(msg)
            // TODO Auto-generated catch block
            return OpenOrderResponse(message = msg, status = -1)
        }
        val response = httpResponse.body.substring(5, httpResponse.body.length - 1)
        log.info("insertOpenOrder response: $response")
        try {
            val openOrderResponse: OpenOrderResponse = mapper.readValue(response)
            if (openOrderResponse.status!=0) return openOrderResponse
            order.orderId = openOrderResponse.orderId.toInt()
            order.openOrderResponse = openOrderResponse
//            order.open_date = OffsetDateTime.now(ZoneOffset.UTC).toLocalDateTime()
            ordersRepository.save(order)

            producer.produce(
                TransactionEvent(
                    o = order.orderId,
                    p = order.positionId,
                    type = TransactionType.PENDING,
                    price = order.open_price,
                    sl = order.stopOrderPrice,
                    tp = order.limitOrderPrice,
                    t = order.open_date?.toEpochSecond(ZoneOffset.UTC)?:0L
                ),
                TOPIC_TRANSACTIONS, null)

            return openOrderResponse
        } catch (e: Exception) {
            val msg = "OpenOrderResponse mapping error: ${e.message}"
            // TODO Auto-generated catch block
            log.error(msg)
            // TODO Auto-generated catch block
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
            val response = httpResponse.body.substring(5, httpResponse.body.length - 1)
            log.info("getOrder response: $response")
            // Possible response: {"__type":"TradingPlatform.OpenOrder","OrderID":null,"QuoteID":null,"MarketID":null,"Market":null,"ExpiryDate":null,"TradeMode":null,"Stake":null,"OrderMode":null,"OrderType":null,"OrderPriceMode":null,"LimitOrderPrice":null,"StopOrderPrice":null,"OrderStatus":null,"IsForceOpen":false,"IDOID":null,"IDOOrderMode":null,"IDOTradeMode":null,"IDOIsGuaranteedStop":false,"IDOLimitOrderPrice":null,"IDOStopOrderPrice":null,"IDOTrailingPoint":null,"Currency":null,"TrailingPoint":null,"IsRollingMarket":false,"Status":-996,"Message":"Session Expired."}
            try {
                val openOrderResponse: OpenOrderResponse = mapper.readValue(response)
                return openOrderResponse
            } catch (e: Exception) {
                log.error("OpenOrderResponse mapping error: $e")
                // TODO Auto-generated catch block
                return null
            }
        } catch (e: Exception) {
            log.error("GetOrder error: ${httpResponse.statusCode} ${httpResponse.body}")
            return null
        }
    }

    fun getOrders(): List<Order> {
        return ordersRepository.findAll()
    }

    fun getHistory(): List<TransactionHistoryOrder>? {
        var httpResponse: HttpAdapterResponse = HttpAdapterResponse(0, "")
        try {
            log.info("GetHistory request")
            httpResponse =
                httpAdapter.postRequest(GET_TRANSACTION_HISTORY, HISTORY_QUERY, RequestHeaders.postHeaders)
            val response = httpResponse.body.substring(10, httpResponse.body.length - 2)    //.replace('/','-')
            log.info("GetHistory response: $response")
            try {
                val transactionHistoryResponse: TransactionHistoryOrders = mapper.readValue(response)
                return transactionHistoryResponse.records
            } catch (e: Exception) {
                log.error("TransactionHistoryResponse mapping error: $e")
                // TODO Auto-generated catch block
                return null
            }
        } catch (e: Exception) {
            log.error("GetHistory error: ${httpResponse.statusCode} ${httpResponse.body}")
            return null
        }
    }

    private fun saveTradeRequest(httpResponse: HttpAdapterResponse, order: Order): TradeRequestResponse {
        val response = httpResponse.body.substring(5, httpResponse.body.length - 1)
        try {
            val tradeRequestResponse: TradeRequestResponse = mapper.readValue(response)
            order.orderId = tradeRequestResponse.orderId.toInt()
            order.tradeRequestResponse = tradeRequestResponse
            order.open_price = tradeRequestResponse.price
            order.open_date = OffsetDateTime.now(ZoneOffset.UTC).toLocalDateTime()
            order.positionId = tradeRequestResponse.positionId
            ordersRepository.save(order)
            return tradeRequestResponse
        } catch (e: Exception) {
            val msg = "TradeRequest mapping error: ${httpResponse.body}"
            log.error(msg)
            log.debug("TradeRequest json: $response")
            // TODO Auto-generated catch block
            return TradeRequestResponse(message = msg, status = -1)
        }
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
