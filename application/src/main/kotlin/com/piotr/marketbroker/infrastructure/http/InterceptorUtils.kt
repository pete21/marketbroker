package com.piotr.marketbroker.infrastructure.http

import org.apache.hc.core5.http.EntityDetails
import org.apache.hc.core5.http.Header
import org.apache.hc.core5.http.HttpRequest
import org.apache.hc.core5.http.HttpResponse
import org.apache.hc.core5.http.protocol.HttpContext

    fun buildRequest(request: HttpRequest, context: HttpContext): String {
        return ("\nRequest: ${context.protocolVersion} ${request.method} ${request.uri}")
    }

fun buildResponse(response: HttpResponse): String {
    return "\nResponse: ${response.code} ${response.reasonPhrase}"
}

    fun buildHeaders(headers: Array<Header>): String {
        return "\nHeaders: [${headers.map { header: Header -> header.name + ": " + header.value }}]"
    }

    fun buildRequestEntity(request: HttpRequest, entity: EntityDetails?): String {
        var content: String = ""
        if (entity!=null) {
            content = "\nContent: ${entity.contentType} ${entity.contentLength}"
        }
        return content
    }

    fun buildResponseEntity(response: HttpResponse, entity: EntityDetails?): String {
        var content: String = ""
        if (entity!=null) {
            content = "\nContent: ${entity.contentType} ${entity.contentLength} "
        }
        return content
    }
