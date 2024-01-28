package com.piotr.marketbroker.infrastructure.http
/*
import mu.KotlinLogging
import org.springframework.stereotype.Component
import java.net.CookieHandler
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

    private val client: HttpClient
    init {
        val cm = CookieManager()
        cm.setCookiePolicy(CookiePolicy.ACCEPT_ALL)
        CookieHandler.setDefault(cm)
        client = HttpClient.newBuilder()
            .version(HttpClient.Version.HTTP_2)
            .followRedirects(HttpClient.Redirect.NORMAL)
//        .cookieHandler(CookieHandler.getDefault())
//            .cookieHandler(cm)
//            .cookieHandler(cm)
            .build()
    }

    var defaultHeaders: RequestHeaders? = null
    var baseUrl: String = ""

    fun postRequest(url: String, body: String, headers: RequestHeaders?): HttpAdapterResponse {
        log.info("postRequest: {}", baseUrl+url)
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
        val response = executeRequest(request, false)
        return response
    }

    private fun builder(url: String, headers: RequestHeaders?): HttpRequest.Builder {
        val requestBuilder = HttpRequest.newBuilder(URI(url))
        if (headers != null) {
            RequestHeaders(defaultHeaders!!, headers).toListPair().forEach {
                run {
                    val (first, second) = it
                    log.info("request header: $first : $second")
                    requestBuilder.headers(first, second)
                }
            }
        } else {
            defaultHeaders!!.toListPair().forEach {
                run {
                    val (first, second) = it
                    log.info("request header: $first : $second")
                    requestBuilder.headers(first, second)
                }
            }
        }
        return requestBuilder
    }

    private fun executeRequest(request: HttpRequest?, muted: Boolean=false): HttpResponse<String> {
        val response = client.send(request, BodyHandlers.ofString())
        if (!muted) {
            log.info("request cookies: ${request!!.headers().allValues("Cookie")}")
            log.info("response body : ${response.body()}")
            log.info("response status code: ${response.statusCode()}")
        }
        return response
    }

}

//data class HttpAdapterResponse (val statusCode: Int, val body: String)
*/