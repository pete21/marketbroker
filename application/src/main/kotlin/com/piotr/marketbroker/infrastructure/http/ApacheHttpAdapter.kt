package com.piotr.marketbroker.infrastructure.http

import mu.KotlinLogging
import org.apache.hc.client5.http.classic.methods.HttpGet
import org.apache.hc.client5.http.classic.methods.HttpOptions
import org.apache.hc.client5.http.classic.methods.HttpPost
import org.apache.hc.client5.http.classic.methods.HttpUriRequestBase
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient
import org.apache.hc.client5.http.impl.classic.HttpClients
import org.apache.hc.client5.http.cookie.BasicCookieStore
import org.apache.hc.client5.http.cookie.CookieStore
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse
import org.apache.hc.client5.http.protocol.HttpClientContext
import org.apache.hc.core5.http.ContentType
import org.apache.hc.core5.http.HttpHeaders
import org.apache.hc.core5.http.io.entity.EntityUtils
import org.apache.hc.core5.http.io.entity.StringEntity
import org.apache.hc.core5.http.message.BasicHeader
import org.springframework.stereotype.Component

private val log = KotlinLogging.logger(ApacheHttpAdapter::class.toString())

@Component
class ApacheHttpAdapter {

    private val cookieStore: CookieStore = BasicCookieStore()
//    private val client: CloseableHttpClient = HttpClients.createDefault()
    private val client: CloseableHttpClient = HttpClients
        .custom()
        .addRequestInterceptorFirst(LoggingRequestInterceptor())
        .addResponseInterceptorLast(LoggingResponseInterceptor())
        .setDefaultCookieStore(cookieStore)
//        .setDefaultHeaders(mutableListOf(BasicHeader(HttpHeaders.CONTENT_LENGTH, "0")))
        .setDefaultHeaders(mutableListOf(BasicHeader(
            HttpHeaders.USER_AGENT,"Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36")))
        .build()
    // Create local HttpClientContext
    private var httpClientContext: HttpClientContext = HttpClientContext.create()

    init {
        // Bind the cookieStore to the localContext
        httpClientContext.cookieStore = cookieStore
        client
    }

    var defaultHeaders: RequestHeaders? = null
    var baseUrl: String = ""

    fun postRequest(url: String, body: String, headers: RequestHeaders?): HttpAdapterResponse {
        val targetUrl = if (url.length>20) { url } else { baseUrl + url }
        log.info("postRequest: {}", targetUrl)
        val response = execute(HttpPost(targetUrl), body.ifEmpty { "{}" }, headers)

        val httpAdapterResponse = HttpAdapterResponse(response.code, EntityUtils.toString(response.entity))
        response.close()
        return httpAdapterResponse
    }

    fun getRequest(url: String, headers: RequestHeaders?) : HttpAdapterResponse {
        log.info("getRequest: $url")
        val response = execute(HttpGet(url), "", headers)
        val httpAdapterResponse = HttpAdapterResponse(response.code, EntityUtils.toString(response.entity))
        response.close()
        return httpAdapterResponse
    }

    fun optionsRequest(url: String, headers: RequestHeaders?) : HttpAdapterResponse {
        log.info("optionsRequest: $url")
        val response = execute(HttpOptions(url), "{}", headers)
        val httpAdapterResponse = HttpAdapterResponse(response.code, "")
        response.close()
        return httpAdapterResponse
    }

    fun getRequestRedirects(url: String, headers: RequestHeaders?) : Pair<List<String>, Map<String,String>> {
        log.info("getRequestWithRedirect: $url")
        val response = execute(HttpGet(url), "", headers)
        val redirectURIs = httpClientContext.redirectLocations.all.map { it.toString()}
        val cookies = httpClientContext.cookieStore.cookies.associateBy( {it.name}, {it.value})
        response.close()
        return Pair(redirectURIs, cookies)
    }

    private fun execute(httpRequest: HttpUriRequestBase, body: String, headers: RequestHeaders?): CloseableHttpResponse {
        if (headers != null) {
            RequestHeaders(defaultHeaders!!, headers).toListPair().forEach {
                run {
                    if (it.second.isEmpty())
                        httpRequest.removeHeaders(it.first)
                    else httpRequest.setHeader(it.first, it.second)
                }
            }
        } else {
            defaultHeaders!!.toListPair().forEach {
                run { httpRequest.setHeader(it.first, it.second) }
            }
        }
        if (body.isNotEmpty()) {
            httpRequest.entity = StringEntity(body, ContentType.APPLICATION_JSON)
        }
//        log.info("request headers: ${request.allHeaders.joinToString("\n", "\n")}")
        return client.execute(httpRequest, httpClientContext)
    }

}

data class HttpAdapterResponse (val statusCode: Int, val body: String)
