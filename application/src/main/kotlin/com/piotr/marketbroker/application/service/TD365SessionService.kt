package com.piotr.marketbroker.application.service

import com.fasterxml.jackson.annotation.JsonAutoDetect
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.PropertyAccessor
import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.piotr.marketbroker.application.event.SessionClosedEvent
import com.piotr.marketbroker.application.event.WebsocketDisconnectedEvent
import com.piotr.marketbroker.application.websocket.WebsocketService
import com.piotr.marketbroker.configuration.td365.TD365ConfigurationProperties
import com.piotr.marketbroker.infrastructure.http.ApacheHttpAdapter
import com.piotr.marketbroker.infrastructure.http.RequestHeaders
import com.piotr.marketbroker.infrastructure.http.RequestHeaders.Companion.authHeaders
import com.piotr.marketbroker.infrastructure.http.RequestHeaders.Companion.loginHeaders
import com.piotr.marketbroker.infrastructure.http.RequestHeaders.Companion.postHeaders
import com.piotr.marketbroker.infrastructure.http.RequestHeaders.Companion.redirectHeaders
import com.piotr.marketbroker.common.logger
import com.piotr.marketbroker.domain.accounts.LiveAccounts
import org.springframework.context.ApplicationEventPublisher
import org.springframework.context.event.EventListener
import org.springframework.http.HttpHeaders
import org.springframework.scheduling.annotation.Async
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service

private const val USER_AUTH = "{\"username\":\"%s\",\"password\":\"%s\"}"

private const val ACCOUNT_ID = "{\"account_id\":%d}"

private const val ACCESS_CONTROL_REQUEST_METHOD = "access-control-request-method"

private const val ACCESS_CONTROL_REQUEST_HEADERS = "access-control-request-headers"

@Service
class TD365SessionService(
    private val td365ConfigurationProperties: TD365ConfigurationProperties,
    private val httpAdapter: ApacheHttpAdapter,
    private val websocketService: WebsocketService,
    private val applicationEventPublisher: ApplicationEventPublisher,
    private val subscriptionsService: SubscriptionsService
) {

    private val log by logger()

    private val mapper = jacksonObjectMapper()
        .setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY)
        .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)

    private var sessionState: Int = 0
    private var liveLogin: Boolean = false

//    private var login : String = ""
    private var ots : String = ""
    private var token : String = ""
    private var jwt : Jwt? = null
    private var liveAccounts: LiveAccounts? = null
    private var selectedAccountId: Int = 0

    fun getTD365ConfigurationProperties(): String {
        log.info(td365ConfigurationProperties.toString())
        return td365ConfigurationProperties.toString()
    }

    @Scheduled(fixedRateString = "\${td365ConfigurationProperties.sessionupdateinterval}")
    @Async
    fun httpClientSessionUpdate() {
        log.info("UpdateClientSessionID")
        if (sessionState == 1) {
            httpAdapter.postRequest("UpdateClientSessionID", "{}", redirectHeaders)
        }
    }

//    fun demoSessionStart(): Boolean {
//        if (liveLogin) {
//            log.warn("Logged in to live account, log out first before starting demo session")
//            return false
//        }
//        if (sessionState==1) {
//            log.warn("Demo Session already started")
//            return false
//        }
//        httpAdapter.baseUrl = td365ConfigurationProperties.demobaseurl
//        httpAdapter.defaultHeaders = RequestHeaders(td365ConfigurationProperties.demoHeaders)
//
//        val pair = httpAdapter.getRequestRedirects(td365ConfigurationProperties.demolink, redirectHeaders)
//        setValues(pair)
//
//        val queryParams = pair.first[0].split("?")[1].split("&")
//        log.info("queryParams: $queryParams")
//        login = queryParams[0].split("=")[1]
//        log.info("login: $login")
//
//        if (websocketService.connect(login, token, td365ConfigurationProperties.demowebsocketserver)) {
//            sessionState = 1
//            return true
//        }
//        return false
//    }

    fun liveLogin(): Boolean {
        if (sessionState==1) {
            log.warn("liveLogin: Session already started")
            return false
        }
        httpAdapter.defaultHeaders = RequestHeaders(td365ConfigurationProperties.prodHeaders)
        if (!tokenAuthentication()) {
            return false
        }
        loginHeaders.setHeader(HttpHeaders.AUTHORIZATION, "Bearer " + jwt!!.access_token)
        httpAdapter.baseUrl = td365ConfigurationProperties.prodbaseurl

        liveLogin = accounts()

        applicationEventPublisher.publishEvent(SessionClosedEvent())            //clear session (subscriptions, kafka-connectors, ...)

        return liveLogin
    }

    @Scheduled(fixedRateString = "\${td365ConfigurationProperties.accesstokenupdateinterval}")
    @Async
    fun reauthenticate() {
        log.info("tokenAuthentication")
        val authenticationResult = tokenAuthentication()
        if (!authenticationResult) {
            log.error("Reauthentication failed")
        }
    }


    @Async
    @EventListener
    fun handleDisconnect(event: WebsocketDisconnectedEvent) {
        log.info("Handling WebsocketDisconnectedEvent")
        if (sessionState==1) {

            val account = liveAccounts!!.app_metadata?.trading_accounts?.first { it?.id == selectedAccountId }
            checkNotNull(account) {"Account not found"}

            val launchUrl = getUrl(selectedAccountId)

            val pair = httpAdapter.getRequestRedirects(launchUrl, redirectHeaders)
            setValues(pair)

            val websocketServer =
                if (account.type == "Demo") {
                    td365ConfigurationProperties.demowebsocketserver
                } else {
                    td365ConfigurationProperties.prodwebsocketserver
                }

            checkNotNull(account.ct_login_id) { "ct_login_id is null, check if account is active and can be logged in" }
            if (!websocketService.connect(account.ct_login_id!!, token, websocketServer)) {
                sessionState = 0
            }
            subscriptionsService.renewSubscriptions()
        }
    }

    fun liveSessionStart(accountId: Int) : Boolean {
        if (!liveLogin) {
            log.error("liveSessionStart: Login required")
            return false
        }
        if (sessionState==1) {
            log.error("liveSessionStart: Session already started")
            return false
        }
        val account = liveAccounts!!.app_metadata?.trading_accounts?.first { it?.id == accountId }
        checkNotNull(account) {"Account not found"}
        log.info("liveSessionStart: $account")

        val launchUrl = getUrl(accountId) + "&lan=1"
        selectedAccountId = accountId

        val websocketServer: String
        if (account.type =="Demo") {
            httpAdapter.baseUrl = td365ConfigurationProperties.demobaseurl
            httpAdapter.defaultHeaders = RequestHeaders(td365ConfigurationProperties.demoHeaders)
            websocketServer = td365ConfigurationProperties.demowebsocketserver
            redirectHeaders.setHeader(HttpHeaders.HOST, "practice.tradenation.com")
        } else {
            httpAdapter.baseUrl = td365ConfigurationProperties.prodbaseurl
            httpAdapter.defaultHeaders = RequestHeaders(td365ConfigurationProperties.prodHeaders)
            websocketServer = td365ConfigurationProperties.prodwebsocketserver
            redirectHeaders.setHeader(HttpHeaders.HOST, "platform.tradenation.com")
        }

        val pair = httpAdapter.getRequestRedirects(launchUrl, redirectHeaders)
        setValues(pair)

        checkNotNull(account.ct_login_id) {"ct_login_id is null, check if account is active and can be logged in"}
        if (websocketService.connect(account.ct_login_id!!, token, websocketServer)) {
            sessionState = 1
            return true
        }
        return false
    }

    private fun getUrl(accountId: Int): String {
        log.info("getUrl for account: $accountId")
        httpAdapter.optionsRequest(td365ConfigurationProperties.loginlink, RequestHeaders(
                loginHeaders, mapOf(
                    ACCESS_CONTROL_REQUEST_METHOD to "GET",
                    ACCESS_CONTROL_REQUEST_HEADERS to "authorization",
                    HttpHeaders.CONTENT_TYPE to "",
                    HttpHeaders.AUTHORIZATION to ""
                )
            )
        )
        val httpResponse = httpAdapter.postRequest(td365ConfigurationProperties.loginlink, String.format(ACCOUNT_ID, accountId), loginHeaders)
        try {
            val redirectUrl: RedirectUrl = mapper.readValue(httpResponse.body)
            log.info("redirectUrl: ${redirectUrl.url}")
            return redirectUrl.url
        } catch (e: JsonProcessingException) {
            log.error("RedirectUrl mapping failed: %s", httpResponse)
            return ""
        }
    }

    private fun setValues(pair: Pair<List<String>, Map<String, String>>) {
        log.info("pair: $pair")

        val redirectURIs = pair.first
        val refererUrl = redirectURIs[redirectURIs.count()-1]
        ots = refererUrl.split("=")[1]
        log.info("ots: $ots")
        token = pair.second[ots].orEmpty()
        log.info("token: $token")
        httpAdapter.defaultHeaders!!.setHeader(HttpHeaders.REFERER, refererUrl)
    }

    fun sessionStop() {
        if (sessionState==1) {
            sessionState = 0
            httpAdapter.postRequest("ClientLogout", "{}", postHeaders)
            websocketService.disconnect()
            applicationEventPublisher.publishEvent(SessionClosedEvent())
            ots = ""
            token = ""
        }
    }

    fun liveLogout() {                                  // https://platform.tradenation.com/logout.aspx
        if (sessionState==0 && liveLogin) {
            liveLogin = false
            jwt = null
            liveAccounts = null
            loginHeaders.removeHeader(HttpHeaders.AUTHORIZATION)
            httpAdapter.baseUrl = ""
            httpAdapter.getRequest(td365ConfigurationProperties.logoutlink, loginHeaders)
            httpAdapter.defaultHeaders = null
        } else {
            log.error("liveLogout: Not logged in or session is active")
        }
    }

    private fun tokenAuthentication(): Boolean {
        val query = String.format(USER_AUTH, td365ConfigurationProperties.username, td365ConfigurationProperties.password)
        // log.debug("query: ${query}")
        val httpResponseDto = httpAdapter.postRequest(td365ConfigurationProperties.authlink, query, authHeaders)
        try {
            log.debug("Jwt: ${httpResponseDto.body}")
            jwt = mapper.readValue(httpResponseDto.body)
        } catch (e: JsonProcessingException) {
            log.error("Jwt mapping failed: %s", httpResponseDto)
            return false
        }
        return true
    }

    private fun accounts(): Boolean {
        httpAdapter.optionsRequest(
            td365ConfigurationProperties.accountlink, RequestHeaders(
                loginHeaders, mapOf(
                    ACCESS_CONTROL_REQUEST_METHOD to "GET",
                    ACCESS_CONTROL_REQUEST_HEADERS to "authorization,baggage,content-type,sentry-trace",
                    HttpHeaders.CONTENT_TYPE to "",
                    HttpHeaders.AUTHORIZATION to ""
                )
            )
        )
        val httpResponse = httpAdapter.getRequest(
            td365ConfigurationProperties.accountlink, loginHeaders
        )
        log.debug(httpResponse.body)
        if (httpResponse.statusCode != 200) {
            return false
        }
        try {
            liveAccounts = mapper.readValue(httpResponse.body)
            log.info("liveAccounts: ${liveAccounts?.toString()}")
        } catch (e: JsonProcessingException) {
            log.error("Accounts mapping failed: ${e.message}")
            return false
        }
        return true
    }

    fun getAccounts(): LiveAccounts? {
        return liveAccounts
    }

}

internal class Jwt (
    @JsonProperty("access_token")
    val access_token: String,

    @JsonProperty("id_token")
    private val id_token: String,

    @JsonProperty("scope")
    private val scope: String,

    @JsonProperty("expires_in")
    private val expires_in: Int,

    @JsonProperty("token_type")
    private val token_type: String
)

internal class RedirectUrl (
    @JsonProperty("url")
    val url: String
)