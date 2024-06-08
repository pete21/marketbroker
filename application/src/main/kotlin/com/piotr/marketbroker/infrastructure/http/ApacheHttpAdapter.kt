package com.piotr.marketbroker.infrastructure.http

import io.micrometer.observation.annotation.Observed
import com.piotr.marketbroker.common.logger
import org.apache.hc.client5.http.classic.methods.HttpDelete
import org.apache.hc.client5.http.classic.methods.HttpGet
import org.apache.hc.client5.http.classic.methods.HttpOptions
import org.apache.hc.client5.http.classic.methods.HttpPost
import org.apache.hc.client5.http.classic.methods.HttpPut
import org.apache.hc.client5.http.classic.methods.HttpUriRequestBase
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient
import org.apache.hc.client5.http.impl.classic.HttpClients
import org.apache.hc.client5.http.cookie.BasicCookieStore
import org.apache.hc.client5.http.cookie.CookieStore
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse
import org.apache.hc.client5.http.protocol.HttpClientContext
import org.apache.hc.core5.http.ContentType
import org.apache.hc.core5.http.io.entity.EntityUtils
import org.apache.hc.core5.http.io.entity.StringEntity
import org.springframework.stereotype.Component

@Component
class ApacheHttpAdapter {

    private val log by logger()

    private val cookieStore: CookieStore = BasicCookieStore()
    private lateinit var client: CloseableHttpClient
    private var httpClientContext: HttpClientContext = HttpClientContext.create()
    var defaultHeaders: RequestHeaders? = null
    var baseUrl: String = ""

    init {
        httpClientContext.cookieStore = cookieStore
        client = HttpClients                            //HttpClients.createDefault()
            .custom()
            .addRequestInterceptorFirst(LoggingRequestInterceptor())
            .addResponseInterceptorLast(LoggingResponseInterceptor())
            .setDefaultCookieStore(cookieStore)
//        .setDefaultHeaders(mutableListOf(BasicHeader(HttpHeaders.CONTENT_LENGTH, "0")))
//        .setDefaultHeaders(mutableListOf(BasicHeader(
//            HttpHeaders.USER_AGENT,"Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36")))
            .build()
    }
    @Observed(name = "ApacheHttpAdapter",
        contextualName = "postRequest",
        lowCardinalityKeyValues = ["type","POST"]
    )
    fun postRequest(url: String, body: String, headers: RequestHeaders?): HttpAdapterResponse {
        val targetUrl = if (url.length>20) { url } else { baseUrl + url }
        log.info("postRequest: $targetUrl")
        val response = execute(HttpPost(targetUrl), body, headers)          //.ifEmpty { "{}" }

        val httpAdapterResponse = HttpAdapterResponse(response.code, EntityUtils.toString(response.entity))
        response.close()
        return httpAdapterResponse
    }

    fun putRequest(url: String, body: String, headers: RequestHeaders?): HttpAdapterResponse {
        val targetUrl = if (url.length>20) { url } else { baseUrl + url }
        log.info("putRequest: $targetUrl")
        val response = execute(HttpPut(targetUrl), body, headers)          //.ifEmpty { "{}" }

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

    fun deleteRequest(url: String, headers: RequestHeaders?) : HttpAdapterResponse {
        log.info("deleteRequest: $url")
        val response = execute(HttpDelete(url), "", headers)
        val httpAdapterResponse = HttpAdapterResponse(response.code, "")
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
        log.info("getRequestRedirects: $url")
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

        return client.execute(httpRequest, httpClientContext)
    }

}

data class HttpAdapterResponse (val statusCode: Int, val body: String)
