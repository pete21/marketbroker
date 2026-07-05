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
import com.piotr.marketbroker.infrastructure.http.RequestHeaders.Companion.loginHeaders
import com.piotr.marketbroker.infrastructure.http.RequestHeaders.Companion.oauthLoginFormHeaders
import com.piotr.marketbroker.infrastructure.http.RequestHeaders.Companion.oauthNavigateHeaders
import com.piotr.marketbroker.infrastructure.http.RequestHeaders.Companion.oauthTokenHeaders
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
import java.net.URLDecoder
import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import java.security.MessageDigest
import java.security.SecureRandom
import java.util.Base64

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

    private var reconnect_attempts: Int = 0

    fun getTD365ConfigurationProperties(): String {
        log.info(td365ConfigurationProperties.toString())
        return td365ConfigurationProperties.toString()
    }

    @Scheduled(fixedRateString = "\${td365.sessionupdateinterval}")
    @Async
    fun httpClientSessionUpdate() {
        log.info("UpdateClientSessionID")
        if (sessionState == 1) {
            httpAdapter.postRequest("UpdateClientSessionID", "{}", redirectHeaders)
        }
    }


    fun liveLogin(): Boolean {
        if (sessionState==1) {
            log.warn("liveLogin: Session already started")
            return false
        }
        if (!tokenAuthentication()) {
            return false
        }
        httpAdapter.defaultHeaders = RequestHeaders(td365ConfigurationProperties.prodHeaders)
        loginHeaders.setHeader(HttpHeaders.AUTHORIZATION, "Bearer " + jwt!!.access_token)
        httpAdapter.baseUrl = td365ConfigurationProperties.prodbaseurl

        liveLogin = accounts()

        applicationEventPublisher.publishEvent(SessionClosedEvent())            //clear session (subscriptions, kafka-connectors, ...)

        return liveLogin
    }

    @Scheduled(fixedRateString = "\${td365.accesstokenupdateinterval}", initialDelayString = "\${td365.accesstokenupdateinterval}")
    @Async
    fun reauthenticate() {
        if (!liveLogin) {
            log.info("Reauthentication not needed, not logged in")
            return
        }
        log.info("tokenAuthentication")
        val authenticationResult = tokenAuthentication()
        if (!authenticationResult) {
            log.error("Reauthentication failed")
        }
        loginHeaders.setHeader(HttpHeaders.AUTHORIZATION, "Bearer " + jwt!!.access_token)

    }


    @Async
    @EventListener
    fun handleDisconnect(event: WebsocketDisconnectedEvent) {
        log.info("Handling WebsocketDisconnectedEvent")
        if (reconnect_attempts > 3) {
            log.error("Reconnect failed, too many attempts")
            sessionStop()
        }
        if (sessionState==1) {

            val account = liveAccounts!!.app_metadata?.trading_accounts?.first { it?.id == selectedAccountId }
            checkNotNull(account) {"Account not found"}

            var launchUrl: String
            try {
                launchUrl = getUrl(selectedAccountId)
            } catch (e: Exception) {
                log.error("getUrl failed: %s", e.message)
                log.error("Retrying authentication...")
                Thread.sleep(5000)
                reauthenticate()
                applicationEventPublisher.publishEvent(WebsocketDisconnectedEvent())
                reconnect_attempts = reconnect_attempts + 1
                return
            }

            try {
                val pair = httpAdapter.getRequestRedirects(launchUrl, redirectHeaders)
                setValues(pair)
            } catch (e: Exception) {
                log.error("getRequestRedirects failed: %s", e.message)
                log.error("Retrying authentication...")
                Thread.sleep(5000)
                reauthenticate()
                applicationEventPublisher.publishEvent(WebsocketDisconnectedEvent())
                reconnect_attempts = reconnect_attempts + 1
                return
            }

            val websocketServer =
                if (account.type == "Demo") {
                    td365ConfigurationProperties.demowebsocketserver
                } else {
                    td365ConfigurationProperties.prodwebsocketserver
                }

            checkNotNull(account.ct_login_id) { "ct_login_id is null, check if account is active and can be logged in" }
            if (!websocketService.connect(account.ct_login_id!!, token, websocketServer)) {
                sessionState = 0
            } else {
                reconnect_attempts = 0
                subscriptionsService.renewSubscriptions()
            }
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

        val launchUrl = getUrl(accountId)
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
            log.info("httpResponse.body: ${httpResponse.body}")
            val redirectUrl: RedirectUrl = mapper.readValue(httpResponse.body)
            log.info("redirectUrl: ${redirectUrl.url}")
            return redirectUrl.url + "&lan=1"
        } catch (e: JsonProcessingException) {
            log.error("RedirectUrl mapping failed: %s", httpResponse)
            return ""
        } catch (e: Exception) {
            log.error("getUrl failed: %s", httpResponse)
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
            
            val account = liveAccounts!!.app_metadata?.trading_accounts?.first { it?.id == selectedAccountId }
            checkNotNull(account) {"Account not found"}
            val logoutUrl = if (account.type == "Demo") {
                loginHeaders.setHeader(HttpHeaders.HOST, "practice.tradenation.com")
                td365ConfigurationProperties.demologoutlink
            } else {
                loginHeaders.setHeader(HttpHeaders.HOST, "platform.tradenation.com")
                td365ConfigurationProperties.logoutlink
            }
            httpAdapter.getRequest(logoutUrl, loginHeaders)
            loginHeaders.setHeader(HttpHeaders.HOST, "portal.cube.finsatechnology.com")

            applicationEventPublisher.publishEvent(SessionClosedEvent())
            ots = ""
            token = ""
        }
    }

    fun liveLogout() : Boolean {                                  // https://platform.tradenation.com/logout.aspx
        return if (sessionState==0 && liveLogin) {
            loginHeaders.removeHeader(HttpHeaders.AUTHORIZATION)
            httpAdapter.getRequest(td365ConfigurationProperties.platformlogoutlink, loginHeaders)
            httpAdapter.baseUrl = ""
            liveLogin = false
            jwt = null
            liveAccounts = null
            httpAdapter.defaultHeaders = null
            applicationEventPublisher.publishEvent(SessionClosedEvent())            //clear session (subscriptions, kafka-connectors, ...) - may be redundant
            true
        } else {
            log.error("liveLogout: Not logged in or session is active")
            false
        }
    }

    private fun tokenAuthentication(): Boolean {
        httpAdapter.clearCookies()
        httpAdapter.defaultHeaders = RequestHeaders(emptyMap())

        val codeVerifier = generateCodeVerifier()
        val codeChallenge = generateCodeChallenge(codeVerifier)
        val state = generateOAuthState()
        val authorizeUrl = buildAuthorizeUrl(codeChallenge, state)

        val loginPageResponse = httpAdapter.getRequest(authorizeUrl, oauthNavigateHeaders)
        if (loginPageResponse.statusCode != 200) {
            log.error("OAuth authorize page request failed: {}", loginPageResponse)
            return false
        }

        val authenticateUrl = extractLoginFormAction(loginPageResponse.body)
        if (authenticateUrl == null) {
            log.error("OAuth login form action not found in authorize page")
            return false
        }

        val loginResponse = httpAdapter.postFormRequestWithRedirects(
            authenticateUrl,
            mapOf(
                "username" to td365ConfigurationProperties.username,
                "password" to td365ConfigurationProperties.password,
                "credentialId" to ""
            ),
            oauthLoginFormHeaders
        )
        val authCode = extractAuthCode(loginResponse.second)
        if (authCode == null) {
            log.error("OAuth authorization code not found, login response: {}", loginResponse.first)
            return false
        }

        val tokenResponse = httpAdapter.postFormRequest(
            td365ConfigurationProperties.oauthtokenurl,
            mapOf(
                "grant_type" to "authorization_code",
                "client_id" to td365ConfigurationProperties.oauthclientid,
                "redirect_uri" to td365ConfigurationProperties.oauthredirecturi,
                "code" to authCode,
                "code_verifier" to codeVerifier
            ),
            oauthTokenHeaders
        )
        if (tokenResponse.statusCode != 200) {
            log.error("OAuth token exchange failed: {}", tokenResponse)
            return false
        }

        try {
            log.debug("Jwt: ${tokenResponse.body}")
            jwt = mapper.readValue(tokenResponse.body)
        } catch (e: JsonProcessingException) {
            log.error("Jwt mapping failed: %s", tokenResponse)
            return false
        }
        return true
    }

    private fun buildAuthorizeUrl(codeChallenge: String, state: String): String {
        val params = linkedMapOf(
            "response_type" to "code",
            "client_id" to td365ConfigurationProperties.oauthclientid,
            "audience" to td365ConfigurationProperties.oauthaudience,
            "redirect_uri" to td365ConfigurationProperties.oauthredirecturi,
            "scope" to "openid",
            "code_challenge" to codeChallenge,
            "code_challenge_method" to "S256",
            "state" to state,
            "ui_locales" to "en",
            "ui_brand" to td365ConfigurationProperties.oauthuibrand,
            "prompt" to "login"
        )
        val query = params.entries.joinToString("&") { (key, value) ->
            "${URLEncoder.encode(key, StandardCharsets.UTF_8)}=${URLEncoder.encode(value, StandardCharsets.UTF_8)}"
        }
        return "${td365ConfigurationProperties.oauthauthorizeurl}?$query"
    }

    private fun extractLoginFormAction(html: String): String? {
        val pattern = Regex("""action="([^"]*login-actions/authenticate[^"]*)"""")
        val action = pattern.find(html)?.groupValues?.get(1)?.replace("&amp;", "&") ?: return null
        return if (action.startsWith("http")) {
            action
        } else {
            "https://auth.tradenation.com${if (action.startsWith("/")) action else "/$action"}"
        }
    }

    private fun extractAuthCode(redirectUrls: List<String>): String? {
        for (url in redirectUrls.asReversed()) {
            extractQueryParam(url, "code")?.let { return it }
        }
        return null
    }

    private fun extractQueryParam(url: String, param: String): String? {
        val query = url.substringAfter('?', "")
        if (query.isEmpty()) {
            return null
        }
        return query.split('&')
            .map { it.split('=', limit = 2) }
            .firstOrNull { it[0] == param }
            ?.getOrNull(1)
            ?.let { URLDecoder.decode(it, StandardCharsets.UTF_8) }
    }

    private fun generateCodeVerifier(): String {
        val bytes = ByteArray(32)
        SecureRandom().nextBytes(bytes)
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes)
    }

    private fun generateCodeChallenge(codeVerifier: String): String {
        val digest = MessageDigest.getInstance("SHA-256").digest(codeVerifier.toByteArray(StandardCharsets.US_ASCII))
        return Base64.getUrlEncoder().withoutPadding().encodeToString(digest)
    }

    private fun generateOAuthState(): String {
        val bytes = ByteArray(16)
        SecureRandom().nextBytes(bytes)
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes)
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

    @JsonProperty("refresh_token")
    val refresh_token: String,

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