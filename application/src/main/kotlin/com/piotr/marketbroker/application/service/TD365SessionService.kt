package com.piotr.marketbroker.application.service

import com.piotr.marketbroker.application.websocket.WebsocketService
import com.piotr.marketbroker.configuration.td365.TD365ConfigurationProperties
import com.piotr.marketbroker.infrastructure.http.ApacheHttpAdapter
import com.piotr.marketbroker.infrastructure.http.RequestHeaders
import io.micrometer.observation.annotation.Observed
import mu.KotlinLogging
import org.springframework.http.HttpHeaders
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service

val log = KotlinLogging.logger(TD365SessionService::class.toString())

@Service
class TD365SessionService(
    private val td365ConfigurationProperties: TD365ConfigurationProperties,
    private val httpAdapter: ApacheHttpAdapter,
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
            httpAdapter.postRequest("UpdateClientSessionID", "", RequestHeaders.redirectHeaders)
        }
    }

    // <demoSessionStart> will be used as a metric name
    // <getting-user-name> will be used as a span  name
    // <userType=userType2> will be set as a tag for both metric & span
    @Observed(name = "TD365SessionService",
        contextualName = "demoSessionStart",
        lowCardinalityKeyValues = ["type","demo"]
    )
    fun demoSessionStart(): Boolean {
        httpAdapter.baseUrl = td365ConfigurationProperties.demobaseurl
        httpAdapter.defaultHeaders = RequestHeaders(td365ConfigurationProperties.demoHeaders)

        val pair = httpAdapter.getRequestRedirects(td365ConfigurationProperties.demolink, RequestHeaders.redirectHeaders)
        val redirectURIs=pair.first
        val cookies=pair.second

        log.info("prevURIs: $redirectURIs")
        log.info("cookies: $cookies")

        val ots: String = redirectURIs[2].split("=")[1]
        log.info("ots: $ots")

        var queryString = redirectURIs[0].split("?")[1]

        val queryParams = queryString.split("&")
        log.info("queryParams: $queryParams")
        login = queryParams[0].split("=")[1]
        log.info("login: $login")
        password = queryParams[1].split("=")[1]
        log.info("password: $password")

        token = cookies[ots].orEmpty()
        log.info("token: $token")
        httpAdapter.defaultHeaders!!.setHeader(HttpHeaders.REFERER, String.format(td365ConfigurationProperties.demoReferer, ots))

        if (websocketService.connect(login, token, td365ConfigurationProperties.demowebsocketserver)) {
            sessionState = 1
            return true
        }
        return false

    }

    fun demoSessionStop(): Boolean {
        httpAdapter.postRequest("ClientLogout", "", RequestHeaders.postHeaders)
        websocketService.disconnect()
        sessionState = 0
        return true
    }

}