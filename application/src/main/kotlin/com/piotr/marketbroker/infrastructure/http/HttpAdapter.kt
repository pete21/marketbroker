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

    var defaultHeaders: RequestHeaders? = null
    var baseUrl: String? = null
//    private val httpClientContext: HttpClientContext = HttpClientContext


    private val client: HttpClient  = HttpClient.newBuilder()
        .version(HttpClient.Version.HTTP_2)
        .followRedirects(HttpClient.Redirect.NORMAL)
//        .cookieHandler(CookieHandler.getDefault())
        .cookieHandler(CookieManager(null, CookiePolicy.ACCEPT_ALL))
        .build()

    fun postRequest(url: String, body: String, headers: RequestHeaders?): HttpAdapterResponse {

        val requestBuilder = HttpRequest.newBuilder(URI(url))
        if (headers!=null)
        RequestHeaders(defaultHeaders!!, headers).toListPair().forEach { it ->
            run {
                val (first, second) = it
                requestBuilder.headers(first, second)
            }
        } else
        {
            requestBuilder.headers(defaultHeaders!!.toString())
        }
        val request = requestBuilder
            .POST(BodyPublishers.ofString(body))
            .build()
        val response = client.send(request, BodyHandlers.ofString())
        val responseBody = response.body()
        val responseStatusCode = response.statusCode()
        println("httpPostRequest : $responseBody")
        println("httpGetRequest status code: $responseStatusCode")
        return HttpAdapterResponse(responseStatusCode, responseBody)
    }

    fun getRequest(url: String, headers: RequestHeaders) : HttpAdapterResponse {
        val requestBuilder = HttpRequest.newBuilder(URI(url))
        RequestHeaders(defaultHeaders!!, headers).toListPair().forEach { it ->
            run {
                val (first, second) = it
                requestBuilder.headers(first, second)
            }
        }
        val request = requestBuilder
            .GET()
            .build()

        val response = client.send(request, BodyHandlers.ofString())

        val responseBody = response.body()
        val responseStatusCode = response.statusCode()

        println("httpGetRequest: $responseBody")
        println("httpGetRequest status code: $responseStatusCode")
        return HttpAdapterResponse(responseStatusCode, responseBody)
    }

    fun getRequestWithRedirect(url: String, headers: RequestHeaders) : HttpResponse<String> {
        log.info("getRequestWithRedirect: $url")
        val requestBuilder = HttpRequest.newBuilder(URI(url))
        RequestHeaders(defaultHeaders!!, headers).toListPair().forEach { it ->
            run {
                val (first, second) = it
                requestBuilder.headers(first, second)
            }
        }
        val request = requestBuilder
            .GET()
            .build()

        val response = client.send(request, BodyHandlers.ofString())
        return response

    }

}

data class HttpAdapterResponse (val statusCode: Int, val body: String)