package com.piotr.marketbroker.application.service

import com.fasterxml.jackson.annotation.JsonAutoDetect
import com.fasterxml.jackson.annotation.PropertyAccessor
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.piotr.marketbroker.application.websocket.WebsocketService
import com.piotr.marketbroker.common.unwrap
import com.piotr.marketbroker.configuration.td365.TD365ConfigurationProperties
import com.piotr.marketbroker.infrastructure.http.HttpAdapter
import com.piotr.marketbroker.infrastructure.http.RequestHeaders
import mu.KotlinLogging
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.http.HttpHeaders
import org.springframework.stereotype.Service
import java.net.http.HttpResponse
import java.util.*

private val log = KotlinLogging.logger {}

@Service
@EnableConfigurationProperties(TD365ConfigurationProperties::class)
class TD365ApiService(
    private val td365ConfigurationProperties: TD365ConfigurationProperties,
    private val httpAdapter: HttpAdapter,
    private val websocketService: WebsocketService
) {

    private val realAccount = false
    private var sessionState = 0

    private var login : String = ""
    private var ots : String = ""
    private var token : String = ""

    private val GET_MARKET_GROUP = "{\"superGroupId\":%d}"

    //    private static String POPULAR_GROUP_QUOTES_QUERY = "{\"groupID\":\"\",\"keyword\":\"\",\"portfolio\":false,\"search\":false,\"popular\":true}";
    private val GROUP_QUOTES_QUERY =
        "{\"groupID\":%d,\"keyword\":\"\",\"portfolio\":false,\"search\":false,\"popular\":false}"

    private val mapper: ObjectMapper = ObjectMapper()
        .setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY)
        .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)

    fun demoSessionStart(): Boolean {
        httpAdapter.baseUrl = td365ConfigurationProperties.demobaseurl
        httpAdapter.defaultHeaders = RequestHeaders(td365ConfigurationProperties.demoHeaders)

        val response =
            httpAdapter.getRequestWithRedirect(td365ConfigurationProperties.demolink, RequestHeaders.redirectHeaders)


        log.info("httpResponse: $response")


        //        log.info("httpResponse status code: " + response.getStatusLine().getStatusCode());
        val prevURIs : MutableList<String> = mutableListOf()
        val cookies : MutableList<String> = mutableListOf()
        var prevResponse: Optional<HttpResponse<String>>? = response.previousResponse()

        while (prevResponse != null && prevResponse.isPresent) {
            prevURIs.add(prevResponse.unwrap()!!.headers().firstValue(HttpHeaders.LOCATION).toString())
            cookies.add(prevResponse.unwrap()!!.headers().firstValue("Set-Cookie").get())
            prevResponse = prevResponse.unwrap()!!.previousResponse()
        }

        log.info("prevURIs: $prevURIs")
        log.info("cookies: $cookies")

            val queryString = prevURIs[0].split("\\?".toRegex()).dropLastWhile { it.isEmpty() }
                .toTypedArray()[1]
        log.info("queryString: $queryString")
            val queryParams = queryString.split("&".toRegex()).dropLastWhile { it.isEmpty() }
                .toTypedArray()
        log.info("queryParams: $queryParams")
            login = queryParams[0].split("=".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()[1]
        log.info("login: $login")

            //            password = queryParams[1].split("=")[1];
            ots = prevURIs[2].toString().split("=".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()[1]
            log.info("ots: $ots")

//            token = cookies.stream()
//                .filter(Predicate<org.apache.http.cookie.Cookie> { c: org.apache.http.cookie.Cookie -> c.getName() == ots })
//                .findFirst().get().getValue()

            //            log.info(cookies.toString());
            log.info("token: $token")
            return true



/*
            if (
            websocketSessionHandler.connect(
                tdHttpClient.login,
                tdHttpClient.token,
                td365ConfigurationProperties.demowebsocketserver
            )
        ) {
            sessionState = 1


            tdHttpClient.headers.put(
                org.apache.http.HttpHeaders.REFERER,
                String.format(
                    if (real) td365ConfigurationProperties.getProdReferer() else td365ConfigurationProperties.getDemoReferer(),
                    tdHttpClient.ots
                )
            )

            return true
        }
        return false
*/

    }

    fun demoSessionStop(): Boolean {
        val response = httpAdapter.postRequest("ClientLogout", "", null)
        websocketService.disconnect()
        sessionState = 0
        return true
    }
}