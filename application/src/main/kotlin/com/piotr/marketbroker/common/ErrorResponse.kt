package com.piotr.marketbroker.common

data class ErrorResponse(
    val status: Int,
    val code: String,
    val message: String?,
    val details: Any?
) {
    companion object {
        fun from(status: Int, code: String, message: String?, details: Any? = null) =
            ErrorResponse(status, code, message, details)
    }
}
