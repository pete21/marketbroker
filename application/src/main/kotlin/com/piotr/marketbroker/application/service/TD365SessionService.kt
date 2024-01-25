package com.piotr.marketbroker.application.service

import com.piotr.marketbroker.application.websocket.WebsocketService
import com.piotr.marketbroker.common.unwrap
import com.piotr.marketbroker.configuration.td365.TD365ConfigurationProperties
import com.piotr.marketbroker.infrastructure.http.HttpAdapter
import com.piotr.marketbroker.infrastructure.http.RequestHeaders
import mu.KotlinLogging
import org.springframework.http.HttpHeaders
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import java.net.http.HttpResponse
import java.util.*

val log = KotlinLogging.logger {}

@Service
class TD365SessionService(
    private val td365ConfigurationProperties: TD365ConfigurationProperties,
    private val httpAdapter: HttpAdapter,
    private val websocketService: WebsocketService
) {

    private val realAccount = false
    private var sessionState = 0

    private var login : String = ""
    private var password : String = ""
    private var ots : String = ""
    private var token : String = ""

    fun getTD365ConfigurationProperties(): String {
        log.info(td365ConfigurationProperties.toString())
        return td365ConfigurationProperties.toString()
    }

    @Scheduled(fixedRateString = "\${td365ConfigurationProperties.sessionupdateinterval}")
    fun httpClientSessionUpdate() {
        if (sessionState == 1) {
            httpAdapter.postRequest("UpdateClientSessionID", "", null)
        }
    }

    fun demoSessionStart(): Boolean {
        httpAdapter.baseUrl = td365ConfigurationProperties.demobaseurl
        httpAdapter.defaultHeaders = RequestHeaders(td365ConfigurationProperties.demoHeaders)

        val response =
            httpAdapter.getRequestWithRedirect(td365ConfigurationProperties.demolink, RequestHeaders.redirectHeaders)

        log.info("httpResponse: $response")

        val prevURIs: MutableList<String> = mutableListOf()
        val cookies: MutableList<String> = mutableListOf()
        var prevResponse: Optional<HttpResponse<String>> = response.previousResponse()

        while (prevResponse.isPresent) {
            prevURIs.add(prevResponse.unwrap()!!.headers().allValues(HttpHeaders.LOCATION)[0])
            cookies.addAll(prevResponse.unwrap()!!.headers().allValues(HttpHeaders.SET_COOKIE))
            prevResponse = prevResponse.unwrap()!!.previousResponse()
        }

        log.info("prevURIs: $prevURIs")
        log.info("cookies: $cookies")

        val ots: String = prevURIs[0].split("=")[1]
        log.info("ots: $ots")

        var queryString = prevURIs[2].split("?")[1]

        val queryParams = queryString.split("&")
        log.info("queryParams: $queryParams")
        login = queryParams[0].split("=")[1]
        log.info("login: $login")
        password = queryParams[1].split("=")[1]
        log.info("password: $password")

        token = cookies.stream()
            .filter { c -> c.split("=")[0] == ots }
            .findFirst().get()
            .split("=")[1]
                .split(";")[0]
        log.info("token: $token")

        if (websocketService.connect(login, token, td365ConfigurationProperties.demowebsocketserver)) {
            sessionState = 1
            httpAdapter.defaultHeaders!!.setHeader(HttpHeaders.REFERER, td365ConfigurationProperties.demoReferer)
            return true
        }
        return false

    }

    fun demoSessionStop(): Boolean {
        httpAdapter.postRequest("ClientLogout", "", null)
        websocketService.disconnect()
        sessionState = 0
        return true
    }

}