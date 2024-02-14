package com.piotr.marketbroker.infrastructure.http

import mu.KotlinLogging
import org.apache.http.HttpHeaders
import org.apache.http.client.config.CookieSpecs
import org.apache.http.client.config.RequestConfig
import org.apache.http.client.methods.CloseableHttpResponse
import org.apache.http.client.methods.RequestBuilder
import org.apache.http.client.protocol.HttpClientContext
import org.apache.http.entity.ContentType
import org.apache.http.entity.StringEntity
import org.apache.http.impl.client.CloseableHttpClient
import org.apache.http.impl.client.HttpClients
import org.apache.http.impl.client.LaxRedirectStrategy
import org.apache.http.util.EntityUtils
import org.springframework.stereotype.Component

private val log = KotlinLogging.logger {}

@Component
class ApacheHttpAdapter {

    private val httpClientContext = HttpClientContext()

    private val client: CloseableHttpClient = HttpClients
        .custom()
        .setDefaultRequestConfig(RequestConfig.custom().setCookieSpec(CookieSpecs.STANDARD).build())
        .setRedirectStrategy(LaxRedirectStrategy())
        .build()

    var defaultHeaders: RequestHeaders? = null
    var baseUrl: String = ""

    fun postRequest(url: String, body: String, headers: RequestHeaders?): HttpAdapterResponse {
        log.info("postRequest: {}", baseUrl+url)
        val response = execute(RequestBuilder.post(baseUrl+url), body, headers)
        return HttpAdapterResponse(response!!.statusLine.statusCode, EntityUtils.toString(response.entity))
    }

    fun getRequest(url: String, headers: RequestHeaders?) : HttpAdapterResponse {
        log.info("getRequest: $url")
        val response = execute(RequestBuilder.get(url), "", headers)
        return HttpAdapterResponse(response!!.statusLine.statusCode, EntityUtils.toString(response.entity))
    }

    fun optionsRequest(url: String, headers: RequestHeaders?) {
        log.info("optionsRequest: $url")
        execute(RequestBuilder.options(url), "", headers)
    }

    fun getRequestRedirects(url: String, headers: RequestHeaders?) : Pair<List<String>, Map<String,String>> {
        log.info("getRequestWithRedirect: $url")
        execute(RequestBuilder.get(url), "", headers)

        val redirectURIs = httpClientContext.redirectLocations.map { it.toString()}
        val cookies = httpClientContext.cookieStore.cookies.associateBy( {it.name}, {it.value})
        return Pair(redirectURIs, cookies)
    }

    private fun execute(requestBuilder: RequestBuilder, body: String, headers: RequestHeaders?): CloseableHttpResponse? {
        if (headers != null) {
            RequestHeaders(defaultHeaders!!, headers).toListPair().forEach {
                run {
                    if (it.second.isEmpty())
                        requestBuilder.removeHeaders(it.first)
                    else requestBuilder.setHeader(it.first, it.second)
                }
            }
        } else {
            defaultHeaders!!.toListPair().forEach {
                run { requestBuilder.setHeader(it.first, it.second) }
            }
        }
        if (body.isNotEmpty()) {
            requestBuilder.setEntity(StringEntity(body, ContentType.APPLICATION_JSON))
        } else {
            requestBuilder.setHeader(HttpHeaders.CONTENT_LENGTH,"0")
        }
        val request = requestBuilder.build()
        log.info("request headers: ${request.allHeaders.joinToString("\n", "\n")}")
        return client.execute(request, httpClientContext)
    }

}

data class HttpAdapterResponse (val statusCode: Int, val body: String)
