package com.piotr.marketbroker.configuration.rest

import com.piotr.marketbroker.common.ErrorResponse
import com.piotr.marketbroker.common.logger
import feign.FeignException
import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatus.BAD_REQUEST
import org.springframework.http.HttpStatus.CONFLICT
import org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR
import org.springframework.http.HttpStatus.NOT_FOUND
import org.springframework.http.ResponseEntity
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.web.bind.MissingRequestHeaderException
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler


@ControllerAdvice
class CustomRestExceptionHandler {

    val log by logger()

    // feign exceptions
    @ExceptionHandler(FeignException::class)
    fun handle(ex: FeignException): ResponseEntity<ErrorResponse> {
        return getResponse(HttpStatus.valueOf(ex.status()), ex, ex.message)
    }

    // general exceptions
    @ExceptionHandler(IllegalArgumentException::class)
    fun handle(ex: IllegalArgumentException): ResponseEntity<ErrorResponse> {
        return getResponse(BAD_REQUEST, ex, ex.message)
    }

    @ExceptionHandler(IllegalStateException::class)
    fun handle(ex: IllegalStateException): ResponseEntity<ErrorResponse> {
        return getResponse(CONFLICT, ex, ex.message)
    }

    @ExceptionHandler(NoSuchElementException::class)
    fun handle(ex: NoSuchElementException): ResponseEntity<ErrorResponse> {
        return getResponse(NOT_FOUND, ex, ex.message)
    }

    @ExceptionHandler(HttpMessageNotReadableException::class)
    fun handle(ex: HttpMessageNotReadableException): ResponseEntity<ErrorResponse> {
        return getResponse(BAD_REQUEST, ex, ex.message)
    }

    @ExceptionHandler(MissingRequestHeaderException::class)
    fun handle(ex: MissingRequestHeaderException): ResponseEntity<ErrorResponse> {
        return getResponse(BAD_REQUEST, ex, ex.message)
    }

    @ExceptionHandler(Exception::class)
    fun handle(ex: Exception): ResponseEntity<ErrorResponse> {
        return getResponse(INTERNAL_SERVER_ERROR, ex, ex.message)
    }


    private fun getResponse(
        status: HttpStatus,
        exception: Exception,
        message: String?
    ): ResponseEntity<ErrorResponse> {
        val response = ErrorResponse.from(status.value(), status.name, message)
        logError(exception)
        return ResponseEntity.status(status).body(response)
    }

    private fun logError(ex: Exception) {
        log.error(ex.message, ex)
    }
}
