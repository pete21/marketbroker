package com.piotr.marketbroker.infrastructure.http

import org.apache.hc.core5.http.HttpHeaders


private const val CROSS_SITE = "cross-site"
private const val SAME_SITE = "same-site"

private const val DOCUMENT = "document"

private const val NAVIGATE = "navigate"

private const val SEC_FETCH_DEST = "Sec-Fetch-Dest"

private const val SEC_FETCH_MODE = "Sec-Fetch-Mode"

private const val SEC_FETCH_USER = "Sec-Fetch-User"

private const val SEC_FETCH_SITE = "Sec-Fetch-Site"

private const val APPLICATION_JSON = "application/json"

private const val USER_AGENT_STRING = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/148.0.0.0 Safari/537.36"

// private const val SEC_CH_UA = "\"Chromium\";v=\"148\", \"Google Chrome\";v=\"148\", \"Not/A)Brand\";v=\"99\""

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

    fun removeHeader(k: String) {
        headers = headers.filterKeys { it!=k }
    }


    companion object {
        val redirectHeaders: RequestHeaders = RequestHeaders(
            mapOf(
//                HttpHeaders.HOST to "platform.tradenation.com",
//                HttpHeaders.REFERER to "https://tradenation.com",
                HttpHeaders.AUTHORIZATION to "",
                HttpHeaders.CONNECTION to "keep-alive",
                SEC_FETCH_DEST to DOCUMENT,
                SEC_FETCH_MODE to NAVIGATE,
                SEC_FETCH_SITE to SAME_SITE,                   // "none" for demo?
                SEC_FETCH_USER to "?1",
                "Upgrade-Insecure-Requests" to "1",
                HttpHeaders.ACCEPT to "text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.7",
                HttpHeaders.ACCEPT_ENCODING to "gzip, deflate, br, zstd",
                HttpHeaders.ACCEPT_LANGUAGE to "en-GB,en;q=0.9,pl;q=0.8,en-US;q=0.7,es;q=0.6",
                HttpHeaders.USER_AGENT to USER_AGENT_STRING
            )
        )

        val authHeaders: RequestHeaders = RequestHeaders(
            mapOf(
                HttpHeaders.HOST to "tradenation.com",
//                "Origin" to "https://tradenation.com",
                SEC_FETCH_DEST to "empty",
                SEC_FETCH_MODE to "cors",
                SEC_FETCH_SITE to CROSS_SITE,
                HttpHeaders.ACCEPT to "*/*",
                HttpHeaders.CONTENT_TYPE to APPLICATION_JSON,
                HttpHeaders.USER_AGENT to USER_AGENT_STRING,
                // "Sec-Ch-Ua-Platform" to "macOS",
                // "Sec-Ch-Ua-Mobile" to "?0",
                // "Sec-Ch-Ua" to SEC_CH_UA,
                HttpHeaders.REFERER to "https://tradenation.com",
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

        val oauthNavigateHeaders: RequestHeaders = RequestHeaders(
            mapOf(
                HttpHeaders.HOST to "auth.tradenation.com",
                HttpHeaders.ACCEPT to "text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.7",
                HttpHeaders.ACCEPT_LANGUAGE to "en-GB,en;q=0.9",
                HttpHeaders.REFERER to "https://tradenation.com/",
                HttpHeaders.USER_AGENT to USER_AGENT_STRING,
                SEC_FETCH_DEST to DOCUMENT,
                SEC_FETCH_MODE to NAVIGATE,
                SEC_FETCH_SITE to SAME_SITE,
                SEC_FETCH_USER to "?1",
                "Upgrade-Insecure-Requests" to "1",
            )
        )

        val oauthLoginFormHeaders: RequestHeaders = RequestHeaders(
            mapOf(
                HttpHeaders.HOST to "auth.tradenation.com",
                HttpHeaders.ACCEPT to "text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.7",
                HttpHeaders.ACCEPT_LANGUAGE to "en-GB,en;q=0.9",
                HttpHeaders.CONTENT_TYPE to "application/x-www-form-urlencoded",
                HttpHeaders.USER_AGENT to USER_AGENT_STRING,
                SEC_FETCH_DEST to DOCUMENT,
                SEC_FETCH_MODE to NAVIGATE,
                SEC_FETCH_SITE to "same-origin",
                SEC_FETCH_USER to "?1",
                "Upgrade-Insecure-Requests" to "1",
            )
        )

        val oauthTokenHeaders: RequestHeaders = RequestHeaders(
            mapOf(
                HttpHeaders.HOST to "auth.tradenation.com",
                HttpHeaders.ACCEPT to "*/*",
                HttpHeaders.ACCEPT_LANGUAGE to "en-GB,en;q=0.9",
                HttpHeaders.CONTENT_TYPE to "application/x-www-form-urlencoded",
                "Origin" to "https://tradenation.com",
                HttpHeaders.REFERER to "https://tradenation.com/",
                HttpHeaders.USER_AGENT to USER_AGENT_STRING,
                SEC_FETCH_DEST to "empty",
                SEC_FETCH_MODE to "cors",
                SEC_FETCH_SITE to SAME_SITE,
            )
        )

        val loginHeaders: RequestHeaders = RequestHeaders(
            mapOf(
                HttpHeaders.HOST to "portal.cube.finsatechnology.com",
//                "Origin" to "https://tradenation.com",
                SEC_FETCH_DEST to "empty",
                SEC_FETCH_MODE to "cors",
                SEC_FETCH_SITE to CROSS_SITE,
                HttpHeaders.ACCEPT to "*/*",
                HttpHeaders.CONTENT_TYPE to APPLICATION_JSON,
                HttpHeaders.USER_AGENT to USER_AGENT_STRING,
                // "Sec-Ch-Ua-Platform" to "macOS",
                // "Sec-Ch-Ua-Mobile" to "?0",
                // "Sec-Ch-Ua" to SEC_CH_UA,
                HttpHeaders.REFERER to "https://tradenation.com",
            )
        )

        val jsonRequestHeaders: RequestHeaders = RequestHeaders(
            mapOf(
                HttpHeaders.HOST to "",
                "Origin" to "",
                HttpHeaders.REFERER to "",
                HttpHeaders.ACCEPT to APPLICATION_JSON,
                HttpHeaders.CONTENT_TYPE to APPLICATION_JSON
            )
        )
    }

}
