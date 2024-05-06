package com.piotr.marketbroker.application.service

import com.fasterxml.jackson.annotation.JsonAutoDetect
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.PropertyAccessor
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.piotr.marketbroker.application.event.handler.AccountDetailsHandler
import com.piotr.marketbroker.application.mapper.OrderMapper.mapOpenOrderResponseToOrderResponseDTO
import com.piotr.marketbroker.application.mapper.OrderMapper.mapOrderToOrderResponseDto
import com.piotr.marketbroker.application.mapper.OrderMapper.mapTradeRequestToOrderResponseDTO
import com.piotr.marketbroker.application.model.OrderResponseDTO
import com.piotr.marketbroker.infrastructure.http.ApacheHttpAdapter
import com.piotr.marketbroker.infrastructure.http.HttpAdapterResponse
import com.piotr.marketbroker.infrastructure.http.RequestHeaders
import com.piotr.marketbroker.infrastructure.persistence.order.OpenOrderResponse
import com.piotr.marketbroker.infrastructure.persistence.order.Order
import com.piotr.marketbroker.infrastructure.persistence.order.SpringDataOrdersRepository
import com.piotr.marketbroker.infrastructure.persistence.order.TradeRequest
import mu.KotlinLogging
import org.springframework.stereotype.Service

private const val INSERT_OPEN_ORDER = "InsertOpenOrder"
private const val GET_OPEN_ORDER = "GetOpenOrder"
private const val DELETE_ORDER = "DeleteOrder"
private const val REQUEST_TRADE = "RequestTrade"
private const val INSERT_CLOSE_POSITION = "InsertClosePosition"

private const val ORDER_QUERY = "{\"orderID\":%d}"

private val log = KotlinLogging.logger(OrdersService::class.toString())

@Service
class OrdersService(
    private val httpAdapter: ApacheHttpAdapter,
    private val ordersRepository: SpringDataOrdersRepository,
    private val accountDetailsHandler: AccountDetailsHandler
) {

    private val mapper = jacksonObjectMapper()
        .setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY)
        .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)

    fun insertClosePosition(order: Order): OrderResponseDTO? {
//        val orderToClose = ordersRepository.findOrderByPositionId(order.positionId)
        if (!accountDetailsHandler.positionExists(order.positionId)) {
            log.warn("InsertClosePosition error: Position ${order.positionId} does not exist")
            return null
        }

        val insertClosePositionRequestDTO = InsertClosePositionRequestDTO(
            order.marketId,
            order.positionId,
            order.quoteId,
            order.price.toString(),
            order.stake,
            order.direction == -1,
            order.key!!
        )

        val httpResponse: HttpAdapterResponse
        try {
            val jsonString = mapper.writeValueAsString(insertClosePositionRequestDTO)
            log.info("insertClosePosition: $jsonString")
            httpResponse = httpAdapter.postRequest(INSERT_CLOSE_POSITION, jsonString, RequestHeaders.postHeaders)
        } catch (e: Exception) {
            log.error("InsertClosePosition error: {}", e.message)
            return null
        }
        val tradeRequst = saveTradeRequest(httpResponse, order)
        return if (tradeRequst!=null) mapTradeRequestToOrderResponseDTO(tradeRequst) else null

    }

    fun requestTrade(order: Order): OrderResponseDTO? {                               //Market order
        val limit: Boolean = order.limitOrderPrice > 0
        val stop: Boolean = order.stopOrderPrice > 0

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
            order.stopOrderPrice,
            order.limitOrderPrice,
            order.trailingPoint,
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
            log.error("requestTrade error: $e")
            // TODO Auto-generated catch block
            return null
        }
        val tradeRequst = saveTradeRequest(httpResponse, order)
        return if (tradeRequst!=null) mapTradeRequestToOrderResponseDTO(tradeRequst) else null
    }

/*
    private void GetOpenOrderDetails(int orderId) {
        String query = String.format(GET_OPEN_ORDER_QUERY, orderId);
        HttpResponseDto httpResponseDto = tdHttpClient.postRequest(GET_OPEN_ORDER, query);
    }
*/
    fun insertOpenOrder(order: Order): OrderResponseDTO? {                      // Open order or Open stop order
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
            iDOStopOrderPrice = order.stopOrderPrice.toString()
        )

        var httpResponse: HttpAdapterResponse = HttpAdapterResponse(0, "")
        try {
            val jsonString = mapper.writeValueAsString(insertOpenOrderRequestDTO)
            log.info("insertOpenOrder request: $jsonString")
            httpResponse = httpAdapter.postRequest(INSERT_OPEN_ORDER, jsonString, RequestHeaders.postHeaders)
        } catch (e: Exception) {
            log.error("RequestTrade error: ${httpResponse.statusCode} ${httpResponse.body}")
            // TODO Auto-generated catch block
            return null
        }
        val response = httpResponse.body.substring(5, httpResponse.body.length - 1)
        log.info("insertOpenOrder response: $response")
        try {
            val openOrderResponse: OpenOrderResponse = mapper.readValue(response)
            if (openOrderResponse.status!=0) return null
            order.openOrderResponse = openOrderResponse
            ordersRepository.save(order)
            return mapOpenOrderResponseToOrderResponseDTO(openOrderResponse)
        } catch (e: Exception) {
            log.error("OpenOrderResponse mapping error: $e")
            // TODO Auto-generated catch block
            return null
        }
    }

    fun deleteOrder(orderId: Int): Boolean {
        try {
            log.info("DeleteOrder request: $orderId")
            val httpResponse =
                httpAdapter.postRequest(DELETE_ORDER, String.format(ORDER_QUERY, orderId), RequestHeaders.postHeaders)
            return httpResponse.statusCode == 200
        } catch (e: Exception) {
            log.error("DeleteOrder error: ${String.format(ORDER_QUERY, orderId)}")
            return false
        }
    }

    fun getOrder(id: Int): OrderResponseDTO? {
        var httpResponse: HttpAdapterResponse = HttpAdapterResponse(0, "")
        try {
            log.info("GetOrder request: $id")
            httpResponse =
                httpAdapter.postRequest(GET_OPEN_ORDER, String.format(ORDER_QUERY, id), RequestHeaders.postHeaders)
            val response = httpResponse.body.substring(5, httpResponse.body.length - 1)
            log.info("getOrder response: $response")
            try {
                val openOrderResponse: OpenOrderResponse = mapper.readValue(response)
                return mapOpenOrderResponseToOrderResponseDTO(openOrderResponse)
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

    fun getOrders(): List<OrderResponseDTO> {
        return ordersRepository.findAll().map { mapOrderToOrderResponseDto(it) }
    }


    private fun saveTradeRequest(httpResponse: HttpAdapterResponse, order: Order): TradeRequest? {
        val response = httpResponse.body.substring(5, httpResponse.body.length - 1)
        try {
            val tradeRequest: TradeRequest = mapper.readValue(response)
            order.tradeRequest = tradeRequest
            ordersRepository.save(order)
            return tradeRequest
        } catch (e: Exception) {
            log.error("TradeRequest mapping error: $e")
            // TODO Auto-generated catch block
            return null
        }
    }

}


private const val userAgentValue = "Safari/537.36"

internal class InsertClosePositionRequestDTO (
    private val marketID: Int,
    private val positionID: Int,
    private val quoteID: Int,
    private val price: String,
    private val stake: Int,
    private val tradeMode: Boolean,
    @JsonProperty("isKaazingFeed")
    private val isKaazingFeed: Boolean = true,
    private val userAgent: String = userAgentValue,
    private val key: String
) {
    internal constructor(m: Int, p: Int, q: Int, price: String, s: Int, t: Boolean, k: String)
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
        false, iDOOrderModeID, iDOLimitOrderPrice, iDOStopOrderPrice)
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
