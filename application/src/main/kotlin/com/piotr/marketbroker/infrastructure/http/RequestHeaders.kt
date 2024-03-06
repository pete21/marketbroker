package com.piotr.marketbroker.infrastructure.http

import org.apache.hc.core5.http.HttpHeaders


private const val CROSS_SITE = "cross-site"

private const val DOCUMENT = "document"

private const val NAVIGATE = "navigate"

private const val SEC_FETCH_DEST = "Sec-Fetch-Dest"

private const val SEC_FETCH_MODE = "Sec-Fetch-Mode"

private const val SEC_FETCH_USER = "Sec-Fetch-User"

private const val SEC_FETCH_SITE = "Sec-Fetch-Site"

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
                SEC_FETCH_DEST to DOCUMENT,
                SEC_FETCH_MODE to NAVIGATE,
                SEC_FETCH_SITE to CROSS_SITE,                   // "none" for demo?
                SEC_FETCH_USER to "?1",
                "Upgrade-Insecure-Requests" to "1",
                HttpHeaders.ACCEPT to "text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.7"
            )
        )

        val authHeaders: RequestHeaders = RequestHeaders(
            mapOf(
                HttpHeaders.HOST to "td365.eu.auth0.com",
                "Origin" to "https://traders.td365.com",
                SEC_FETCH_DEST to "empty",
                SEC_FETCH_MODE to "cors",
                SEC_FETCH_SITE to CROSS_SITE,
                HttpHeaders.ACCEPT to "*/*",
                HttpHeaders.CONTENT_TYPE to "application/json"
            )
        )

        val postHeaders: RequestHeaders = RequestHeaders(
            mapOf(
                HttpHeaders.CONTENT_TYPE to "application/json; charset=utf-8",

//                HttpHeaders.CONNECTION to "keep-alive",
//                "DNT" to "1",
                SEC_FETCH_DEST to "empty",
                SEC_FETCH_MODE to "cors",
                SEC_FETCH_SITE to "same-origin",
                //        .setHeader("Sec-Fetch-User", "?1")
                //        .setHeader("Upgrade-Insecure-Requests", "1")
                HttpHeaders.ACCEPT to "*/*",
                "X-Requested-With" to "XMLHttpRequest"
            )
        )

        val loginHeaders: RequestHeaders = RequestHeaders(
            mapOf(
                HttpHeaders.HOST to "portal-api.tradenation.com",
                "Origin" to "https://traders.td365.com",
                SEC_FETCH_DEST to "empty",
                SEC_FETCH_MODE to "cors",
                SEC_FETCH_SITE to CROSS_SITE,
                HttpHeaders.ACCEPT to "*/*",
                HttpHeaders.CONTENT_TYPE to "application/json"
            )
        )

    }

}

