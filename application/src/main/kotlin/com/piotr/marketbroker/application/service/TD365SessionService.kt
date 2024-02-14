package com.piotr.marketbroker.application.service

import com.fasterxml.jackson.annotation.JsonAutoDetect
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.PropertyAccessor
import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.piotr.marketbroker.application.websocket.WebsocketService
import com.piotr.marketbroker.configuration.td365.TD365ConfigurationProperties
import com.piotr.marketbroker.infrastructure.http.ApacheHttpAdapter
import com.piotr.marketbroker.infrastructure.http.RequestHeaders
import com.piotr.marketbroker.infrastructure.http.RequestHeaders.Companion.authHeaders
import com.piotr.marketbroker.infrastructure.http.RequestHeaders.Companion.loginHeaders
import io.micrometer.observation.annotation.Observed
import mu.KotlinLogging
import org.springframework.http.HttpHeaders
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service

private val log = KotlinLogging.logger(TD365SessionService::class.toString())

private const val USER_AUTH =
    "{\"realm\":\"Username-Password-Authentication\",\"client_id\":\"eeXrVwSMXPZ4pJpwStuNyiUa7XxGZRX9\",\"scope\":\"openid\",\"grant_type\":\"http://auth0.com/oauth/grant-type/password-realm\",\"username\":\"%s\",\"password\":\"%s\"}"


private const val ACCESS_CONTROL_REQUEST_METHOD = "access-control-request-method"

private const val ACCESS_CONTROL_REQUEST_HEADERS = "access-control-request-headers"

@Service
class TD365SessionService(
    private val td365ConfigurationProperties: TD365ConfigurationProperties,
    private val httpAdapter: ApacheHttpAdapter,
    private val websocketService: WebsocketService
) {

    private val mapper = jacksonObjectMapper()
        .setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY)
        .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)

    private var sessionState = 0

    private var login : String = ""
    private var password : String = ""
    private var ots : String = ""
    private var token : String = ""
    private var jwt : Jwt? = null
    private var accounts: RealAccounts? = null

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
        if (sessionState==1) {
            log.warn("Session already connected")
            return false
        }
        httpAdapter.baseUrl = td365ConfigurationProperties.demobaseurl
        httpAdapter.defaultHeaders = RequestHeaders(td365ConfigurationProperties.demoHeaders)

        val pair =
            httpAdapter.getRequestRedirects(td365ConfigurationProperties.demolink, RequestHeaders.redirectHeaders)
        setValues(pair)

        httpAdapter.defaultHeaders!!.setHeader(HttpHeaders.REFERER,
            String.format(td365ConfigurationProperties.demoReferer, ots))

        if (websocketService.connect(login, token, td365ConfigurationProperties.demowebsocketserver)) {
            sessionState = 1
            return true
        }
        return false
    }

    @Observed(name = "TD365SessionService",
        contextualName = "liveSessionStart",
        lowCardinalityKeyValues = ["type","live"]
    )
    fun liveSessionStart(): Boolean {
        if (sessionState==1) {
            log.warn("Session already connected")
            return false
        }
        httpAdapter.baseUrl = td365ConfigurationProperties.prodbaseurl
        httpAdapter.defaultHeaders = RequestHeaders(td365ConfigurationProperties.prodHeaders)

        val pair =
            httpAdapter.getRequestRedirects(td365ConfigurationProperties.prodlink, RequestHeaders.redirectHeaders)
        setValues(pair)

        httpAdapter.defaultHeaders!!.setHeader(HttpHeaders.REFERER,
            String.format(td365ConfigurationProperties.prodReferer, ots))

        tokenAuthentication()
        httpAdapter.defaultHeaders!!.setHeader(HttpHeaders.AUTHORIZATION, "Bearer " + jwt!!.access_token)
        if (!login() || !accounts() || !launch()) {
            return false
        }
        if (websocketService.connect(login, token, td365ConfigurationProperties.prodwebsocketserver)) {
            sessionState = 1
            return true
        }
        return false
    }

    private fun setValues(pair: Pair<List<String>, Map<String, String>>) {
        val redirectURIs = pair.first
        val cookies = pair.second

        log.info("prevURIs: $redirectURIs")
        log.info("cookies: $cookies")

        ots = redirectURIs[2].split("=")[1]
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
    }

    fun sessionStop(): Boolean {
        httpAdapter.postRequest("ClientLogout", "", RequestHeaders.postHeaders)
        websocketService.disconnect()
        sessionState = 0
        return true
    }

    private fun tokenAuthentication(): Boolean {
        httpAdapter.optionsRequest(td365ConfigurationProperties.authlink, RequestHeaders(authHeaders, mapOf(
            ACCESS_CONTROL_REQUEST_METHOD to "POST",
            ACCESS_CONTROL_REQUEST_HEADERS to "content-type"
        )))
        val query = String.format(USER_AUTH, td365ConfigurationProperties.username, td365ConfigurationProperties.password)
        val httpResponseDto = httpAdapter.postRequest(
            td365ConfigurationProperties.authlink, query, authHeaders)
        try {
            jwt = mapper.readValue(httpResponseDto.body)
        } catch (e: JsonProcessingException) {
            log.error("Jwt mapping failed: ", httpResponseDto)
            return false
        }
        return true
    }

    private fun login(): Boolean {
        httpAdapter.optionsRequest(
            td365ConfigurationProperties.accountlink + "login/", RequestHeaders(
                loginHeaders, mapOf(
                    ACCESS_CONTROL_REQUEST_METHOD to "POST",
                    ACCESS_CONTROL_REQUEST_HEADERS to "authorization,content-type"
                )
            )
        )
        val httpResponse = httpAdapter.postRequest(
            td365ConfigurationProperties.accountlink + "login/", "{}", loginHeaders
        )
        return httpResponse.statusCode.equals(200)
    }

    private fun accounts(): Boolean {
        httpAdapter.optionsRequest(
            td365ConfigurationProperties.accountlink + "accounts/", RequestHeaders(
                loginHeaders, mapOf(
                    ACCESS_CONTROL_REQUEST_METHOD to "GET",
                    ACCESS_CONTROL_REQUEST_HEADERS to "authorization"
                )
            )
        )
        val httpResponse = httpAdapter.getRequest(
            td365ConfigurationProperties.accountlink + "accounts/", loginHeaders
        )
        if (!httpResponse.statusCode.equals(200)) {
            return false
        }
        try {
            accounts = mapper.readValue(httpResponse.body)
        } catch (e: JsonProcessingException) {
            log.error("Accounts mapping failed: ", httpResponse)
            return false
        }
        return true
    }

    private fun launch(): Boolean {
        httpAdapter.optionsRequest(
            td365ConfigurationProperties.accountlink + "launch/", RequestHeaders(
                loginHeaders, mapOf(
                    ACCESS_CONTROL_REQUEST_METHOD to "GET",
                    ACCESS_CONTROL_REQUEST_HEADERS to "authorization"
                )
            )
        )
        var httpResponse = httpAdapter.getRequest(
            td365ConfigurationProperties.accountlink + "launch/", loginHeaders)
        if (!httpResponse.statusCode.equals(200)) {
            return false
        }
        //{"url":"https://cloudtrade.tradedirect365.com/finlogin/loginagent.aspx?aid=1&cid=4266738&tid=52031995&aip=1.1.1.1"}
        val url = httpResponse.body.substring(8, httpResponse.body.length - 2)

        httpResponse = httpAdapter.getRequest(url, RequestHeaders.redirectHeaders)
        return httpResponse.statusCode.equals(200)
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
