package com.piotr.marketbroker.infrastructure.http

import org.apache.http.HttpHeaders


private const val CROSS_SITE = "cross-site"

private const val DOCUMENT = "document"

private const val NAVIGATE = "navigate"

data class RequestHeaders(var headers: Map<String, String>) {
    fun toListPair(): List<Pair<String, String>> {
        return headers.toList()
    }

    constructor(h: RequestHeaders, newHeaders: Map<String, String>)
            : this(h.headers.plus(newHeaders))

    constructor(h: RequestHeaders, newHeaders: RequestHeaders)
            : this(h.headers.plus(newHeaders.toListPair()))


    fun setHeader(k: String, v:String) {
        headers = headers.plus(Pair(k,v))
    }
/*
    fun removeHeader(k: String) {
        headers.keys.remove(k)
    }
 */

    companion object {
        val redirectHeaders: RequestHeaders = RequestHeaders(
            mapOf(
                HttpHeaders.AUTHORIZATION to "",
                HttpHeaders.CONNECTION to "keep-alive",
//                "DNT" to "1",
                "Sec-Fetch-Dest" to DOCUMENT,
                "Sec-Fetch-Mode" to NAVIGATE,
                "Sec-Fetch-Site" to CROSS_SITE,                   // "none" for demo?
                "Sec-Fetch-User" to "?1",
                "Upgrade-Insecure-Requests" to "1",
                HttpHeaders.ACCEPT to "text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.7"
            )
        )

        val authHeaders: RequestHeaders = RequestHeaders(
            mapOf(
                HttpHeaders.HOST to "td365.eu.auth0.com",
//                HttpHeaders.HOST to "",
                "Origin" to "https://traders.td365.com",
                HttpHeaders.REFERER to "https://traders.td365.com/",
                "Sec-Fetch-Dest" to "empty",
                "Sec-Fetch-Mode" to "cors",
                "Sec-Fetch-Site" to CROSS_SITE
            )
        )

        val postHeaders: RequestHeaders = RequestHeaders(
            mapOf(
                HttpHeaders.CONTENT_TYPE to "application/json; charset=utf-8",

//                HttpHeaders.CONNECTION to "keep-alive",
//                "DNT" to "1",
                "Sec-Fetch-Dest" to "empty",
                "Sec-Fetch-Mode" to "cors",
                "Sec-Fetch-Site" to "same-origin",
                //        .setHeader("Sec-Fetch-User", "?1")
                //        .setHeader("Upgrade-Insecure-Requests", "1")
                HttpHeaders.ACCEPT to "*/*",
                "X-Requested-With" to "XMLHttpRequest"
            )
        )

        val loginHeaders: RequestHeaders = RequestHeaders(
            mapOf(
                HttpHeaders.HOST to "portal-api.tradenation.com",
//                HttpHeaders.HOST to "",
                        "Origin" to "https://traders.td365.com",
                HttpHeaders.REFERER to "https://traders.td365.com/",
                "Sec-Fetch-Dest" to "empty",
                "Sec-Fetch-Mode" to "cors",
                "Sec-Fetch-Site" to CROSS_SITE,
                "access-control-request-method" to "POST",
                "access-control-request-headers" to "authorization,content-type"
//                "TE" to "trailers"
            )
        )

    }

}

