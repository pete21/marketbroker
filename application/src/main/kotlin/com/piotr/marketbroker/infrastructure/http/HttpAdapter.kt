package com.piotr.marketbroker.infrastructure.http

import mu.KotlinLogging
import org.springframework.stereotype.Component
import java.net.CookieManager
import java.net.CookiePolicy
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpRequest.BodyPublishers
import java.net.http.HttpResponse
import java.net.http.HttpResponse.BodyHandlers

private val log = KotlinLogging.logger {}

@Component
class HttpAdapter {

    init {
        System.setProperty("jdk.httpclient.allowRestrictedHeaders", "Host")
    }
    var defaultHeaders: RequestHeaders? = null
    var baseUrl: String = ""
//    private val httpClientContext: HttpClientContext = HttpClientContext


    private val client: HttpClient  = HttpClient.newBuilder()
        .version(HttpClient.Version.HTTP_2)
        .followRedirects(HttpClient.Redirect.NORMAL)
//        .cookieHandler(CookieHandler.getDefault())
        .cookieHandler(CookieManager(null, CookiePolicy.ACCEPT_ALL))
        .build()

    fun postRequest(url: String, body: String, headers: RequestHeaders?): HttpAdapterResponse {
        log.info("postRequest: $url")
        val request = builder(baseUrl+url, headers)
            .POST(BodyPublishers.ofString(body))
            .build()
        val response = executeRequest(request)
        return HttpAdapterResponse(response.statusCode(), response.body())
    }

    fun getRequest(url: String, headers: RequestHeaders?) : HttpAdapterResponse {
        log.info("getRequest: $url")
        val request = builder(url, headers)
            .GET()
            .build()
        val response = executeRequest(request)
        return HttpAdapterResponse(response.statusCode(), response.body())
    }

    fun getRequestWithRedirect(url: String, headers: RequestHeaders?) : HttpResponse<String> {
        log.info("getRequestWithRedirect: $url")
        val request = builder(url, headers)
            .GET()
            .build()
        val response = executeRequest(request)
        return response
    }

    private fun builder(url: String, headers: RequestHeaders?): HttpRequest.Builder {
        val requestBuilder = HttpRequest.newBuilder(URI(url))
        if (headers != null) {
            RequestHeaders(defaultHeaders!!, headers).toListPair().forEach {
                run {
                    val (first, second) = it
                    log.info("Header: $first : $second")
                    requestBuilder.headers(first, second)
                }
            }
        } else {
            defaultHeaders!!.toListPair().forEach {
                run {
                    val (first, second) = it
                    log.info("Header: $first : $second")
                    requestBuilder.headers(first, second)
                }
            }
        }
        return requestBuilder
    }

    private fun executeRequest(request: HttpRequest?): HttpResponse<String> {
        val response = client.send(request, BodyHandlers.ofString())
        log.info("response body : ${response.body()}")
        log.info("response status code: ${response.statusCode()}")
        return response
    }

}

data class HttpAdapterResponse (val statusCode: Int, val body: String)
