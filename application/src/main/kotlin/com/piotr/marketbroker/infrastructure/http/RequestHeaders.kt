package com.piotr.marketbroker.infrastructure.http

import org.springframework.http.HttpHeaders


data class RequestHeaders(val headers: Map<String, String>) {
    fun toListPair(): List<Pair<String, String>> {
        return headers.toList()
    }

    constructor(h: RequestHeaders, newHeaders: Map<String, String>)
            : this(h.headers.plus(newHeaders))

    constructor(h: RequestHeaders, newHeaders: RequestHeaders)
            : this(h.headers.plus(newHeaders.toListPair()))

/*
    fun setHeader(k: String, v:String) {
        headers[k] = v
    }

    fun removeHeader(k: String) {
        headers.keys.remove(k)
    }
 */

    companion object {
        val redirectHeaders: RequestHeaders = RequestHeaders(
            mapOf(
                HttpHeaders.ACCEPT_ENCODING to "gzip, deflate, br",
                HttpHeaders.USER_AGENT to "Mozilla/5.0 (Windows NT 6.1; Win64; x64; rv:103.0) Gecko/20100101 Firefox/103.0",
                HttpHeaders.ACCEPT_LANGUAGE to "en-GB,en;q=0.5",
                HttpHeaders.CONNECTION to "keep-alive",
                "DNT" to "1",
                "Sec-Fetch-Dest" to "document",
                "Sec-Fetch-Mode" to "navigate",
                "Sec-Fetch-Site" to "none",
                "Sec-Fetch-User" to "?1",
                "Upgrade-Insecure-Requests" to "1",
                HttpHeaders.ACCEPT to "text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,*/*;q=0.8"
            )
        )

        val authHeaders: RequestHeaders = RequestHeaders(
            mapOf(
                HttpHeaders.HOST to "td365.eu.auth0.com",
                "Origin" to "https://traders.td365.com",
                HttpHeaders.REFERER to "https://traders.td365.com/",
                "Sec-Fetch-Site" to "cross-site",
                "TE" to "trailers"
            )
        )

        val postHeaders: RequestHeaders = RequestHeaders(
            mapOf(
                HttpHeaders.CONTENT_TYPE to "application/json; charset=utf-8",
                HttpHeaders.ACCEPT_ENCODING to "gzip, deflate, br",
                HttpHeaders.USER_AGENT to "Mozilla/5.0 (Windows NT 6.1; Win64; x64; rv:103.0) Gecko/20100101 Firefox/103.0",
                HttpHeaders.ACCEPT_LANGUAGE to "en-GB,en;q=0.5",
                HttpHeaders.CONNECTION to "keep-alive",

                "DNT" to "1",
                "Sec-Fetch-Dest" to "empty",
                "Sec-Fetch-Mode" to "cors",
                "Sec-Fetch-Site" to "same-origin",
                //        .setHeader("Sec-Fetch-User", "?1")
                //        .setHeader("Upgrade-Insecure-Requests", "1")
                HttpHeaders.ACCEPT to "*/*",
                "X-Requested-With" to "XMLHttpRequest"
            )
        )
    }

}

