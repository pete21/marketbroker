package com.piotr.marketbroker.application.service

import com.fasterxml.jackson.annotation.JsonAutoDetect
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.PropertyAccessor
import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.piotr.marketbroker.application.event.SessionClosedEvent
import com.piotr.marketbroker.application.websocket.WebsocketService
import com.piotr.marketbroker.configuration.td365.TD365ConfigurationProperties
import com.piotr.marketbroker.infrastructure.http.ApacheHttpAdapter
import com.piotr.marketbroker.infrastructure.http.RequestHeaders
import com.piotr.marketbroker.infrastructure.http.RequestHeaders.Companion.authHeaders
import com.piotr.marketbroker.infrastructure.http.RequestHeaders.Companion.loginHeaders
import com.piotr.marketbroker.infrastructure.http.RequestHeaders.Companion.postHeaders
import com.piotr.marketbroker.infrastructure.http.RequestHeaders.Companion.redirectHeaders
import io.micrometer.observation.annotation.Observed
import com.piotr.marketbroker.common.logger
import org.springframework.context.ApplicationEventPublisher
import org.springframework.http.HttpHeaders
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service


private const val USER_AUTH =
    "{\"realm\":\"Username-Password-Authentication\",\"client_id\":\"eeXrVwSMXPZ4pJpwStuNyiUa7XxGZRX9\",\"scope\":\"openid\",\"grant_type\":\"http://auth0.com/oauth/grant-type/password-realm\",\"username\":\"%s\",\"password\":\"%s\"}"

private const val ACCESS_CONTROL_REQUEST_METHOD = "access-control-request-method"

private const val ACCESS_CONTROL_REQUEST_HEADERS = "access-control-request-headers"

@Service
class TD365SessionService(
    private val td365ConfigurationProperties: TD365ConfigurationProperties,
    private val httpAdapter: ApacheHttpAdapter,
    private val websocketService: WebsocketService,
    private val applicationEventPublisher: ApplicationEventPublisher
) {

    private val log by logger()

    private val mapper = jacksonObjectMapper()
        .setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY)
        .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)

    private var sessionState: Int = 0
    private var liveLogin: Boolean = false

    private var login : String = ""
//    private var password : String = ""
    private var ots : String = ""
    private var token : String = ""
    private var jwt : Jwt? = null
    private var liveAccounts: LiveAccounts? = null

    @Observed(name = "TD365SessionService",
        contextualName = "getTD365ConfigurationProperties",
        lowCardinalityKeyValues = ["type","config"]
    )
    fun getTD365ConfigurationProperties(): String {
        log.info(td365ConfigurationProperties.toString())
        return td365ConfigurationProperties.toString()
    }

    @Scheduled(fixedRateString = "\${td365ConfigurationProperties.sessionupdateinterval}")
    fun httpClientSessionUpdate() {
        if (sessionState == 1) {
            httpAdapter.postRequest("UpdateClientSessionID", "{}", redirectHeaders)
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
        if (liveLogin) {
            log.warn("Logged in to live account, log out first before starting demo session")
            return false
        }
        if (sessionState==1) {
            log.warn("Demo Session already started")
            return false
        }
        httpAdapter.baseUrl = td365ConfigurationProperties.demobaseurl
        httpAdapter.defaultHeaders = RequestHeaders(td365ConfigurationProperties.demoHeaders)

        val pair =
            httpAdapter.getRequestRedirects(td365ConfigurationProperties.demolink, redirectHeaders)
        setValues(pair)

        var queryParams = pair.first[0].split("?")[1].split("&")
        log.info("queryParams: $queryParams")
        login = queryParams[0].split("=")[1]
        log.info("login: $login")

        if (websocketService.connect(login, token, td365ConfigurationProperties.demowebsocketserver)) {
            sessionState = 1
            return true
        }
        return false
    }

    @Observed(name = "TD365SessionService",
        contextualName = "liveLogin",
        lowCardinalityKeyValues = ["type","live"]
    )
    fun liveLogin(): Boolean {
        if (sessionState==1) {
            log.warn("liveLogin: Session already started")
            return false
        }
        httpAdapter.baseUrl = td365ConfigurationProperties.prodbaseurl
        httpAdapter.defaultHeaders = RequestHeaders(td365ConfigurationProperties.prodHeaders)

        if (!tokenAuthentication()) {
            return false
        }
        loginHeaders.setHeader(HttpHeaders.AUTHORIZATION, "Bearer " + jwt!!.access_token)

        liveLogin = login() && accounts()
        return liveLogin
    }

    @Observed(name = "TD365SessionService",
        contextualName = "liveSessionStart",
        lowCardinalityKeyValues = ["type","live"]
    )
    fun liveSessionStart(accountId: Int) : Boolean {
        if (!liveLogin) {
            log.error("liveSessionStart: Login required")
            return false
        }
        if (sessionState==1) {
            log.error("liveSessionStart: Session already started")
            return false
        }
            val account = liveAccounts!!.results.first {it.id==accountId}
        val launchUrl = getUrl(accountId)

        var websocketServer: String
        if (account.accountType=="DEMO") {
            httpAdapter.defaultHeaders = RequestHeaders(td365ConfigurationProperties.demoHeaders)
            websocketServer = td365ConfigurationProperties.demowebsocketserver
        } else {
            httpAdapter.defaultHeaders = RequestHeaders(td365ConfigurationProperties.prodHeaders)
            websocketServer = td365ConfigurationProperties.prodwebsocketserver
        }

        val pair =
            httpAdapter.getRequestRedirects(launchUrl, redirectHeaders)
        setValues(pair)

        if (websocketService.connect(account.ctLoginId, token, websocketServer)) {
            sessionState = 1
            return true
        }
        return false
    }

    private fun getUrl(accountId: Int): String {
        val accountLink = String.format(td365ConfigurationProperties.prodlink, accountId)
        val optionsResponseDto = httpAdapter.optionsRequest(accountLink, RequestHeaders(
                loginHeaders, mapOf(
                    ACCESS_CONTROL_REQUEST_METHOD to "GET",
                    ACCESS_CONTROL_REQUEST_HEADERS to "authorization",
                    HttpHeaders.CONTENT_TYPE to "",
                    HttpHeaders.AUTHORIZATION to ""
                )
            )
        )
        val httpResponse = httpAdapter.getRequest(accountLink, loginHeaders)
        try {
            val redirectUrl: RedirectUrl = mapper.readValue(httpResponse.body)
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

    @Observed(name = "TD365SessionService",
        contextualName = "sessionStop",
        lowCardinalityKeyValues = ["type","all"]
    )
    fun sessionStop() {
        if (sessionState==1) {
            httpAdapter.postRequest("ClientLogout", "{}", postHeaders)
            websocketService.disconnect()
            applicationEventPublisher.publishEvent(SessionClosedEvent())
            sessionState = 0
            ots = ""
            token = ""
        }
    }

    @Observed(name = "TD365SessionService",
        contextualName = "liveLogout",
        lowCardinalityKeyValues = ["type","live"]
    )
    fun liveLogout() {
        if (sessionState==0 && liveLogin) {
            liveLogin = false
            jwt = null
            liveAccounts = null
            loginHeaders.removeHeader(HttpHeaders.AUTHORIZATION)
            httpAdapter.baseUrl = ""
            httpAdapter.defaultHeaders = null
        } else {
            log.error("liveLogout: Not logged in or session is active")
        }
    }

    private fun tokenAuthentication(): Boolean {
        val optionsResponseDto = httpAdapter.optionsRequest(td365ConfigurationProperties.authlink,
                RequestHeaders(authHeaders, mapOf(
                    ACCESS_CONTROL_REQUEST_METHOD to "POST",
                    ACCESS_CONTROL_REQUEST_HEADERS to "content-type",
                    HttpHeaders.CONTENT_TYPE to ""
                )))
        val query = String.format(USER_AUTH, td365ConfigurationProperties.username, td365ConfigurationProperties.password)
        val httpResponseDto = httpAdapter.postRequest(td365ConfigurationProperties.authlink, query, authHeaders)
        try {
            jwt = mapper.readValue(httpResponseDto.body)
        } catch (e: JsonProcessingException) {
            log.error("Jwt mapping failed: %s", httpResponseDto)
            return false
        }
        return true
    }

    private fun login(): Boolean {
        httpAdapter.optionsRequest(
            td365ConfigurationProperties.accountlink + "login/", RequestHeaders(
                loginHeaders, mapOf(
                    ACCESS_CONTROL_REQUEST_METHOD to "POST",
                    ACCESS_CONTROL_REQUEST_HEADERS to "authorization,content-type",
                    HttpHeaders.CONTENT_TYPE to "",
                    HttpHeaders.AUTHORIZATION to ""
                )
            )
        )
        val httpResponse = httpAdapter.postRequest(
            td365ConfigurationProperties.accountlink + "login/", "{}", loginHeaders
        )
        return httpResponse.statusCode == 200
    }

    private fun accounts(): Boolean {
        httpAdapter.optionsRequest(
            td365ConfigurationProperties.accountlink + "accounts/", RequestHeaders(
                loginHeaders, mapOf(
                    ACCESS_CONTROL_REQUEST_METHOD to "GET",
                    ACCESS_CONTROL_REQUEST_HEADERS to "authorization",
                    HttpHeaders.CONTENT_TYPE to "",
                    HttpHeaders.AUTHORIZATION to ""
                )
            )
        )
        val httpResponse = httpAdapter.getRequest(
            td365ConfigurationProperties.accountlink + "accounts/", loginHeaders
        )
        if (httpResponse.statusCode != 200) {
            return false
        }
        try {
            liveAccounts = mapper.readValue(httpResponse.body)
        } catch (e: JsonProcessingException) {
            log.error("Accounts mapping failed: %s", httpResponse)
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