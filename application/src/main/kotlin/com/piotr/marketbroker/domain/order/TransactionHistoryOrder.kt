package com.piotr.marketbroker.domain.order

import com.fasterxml.jackson.annotation.JsonFormat
import java.time.LocalDateTime

data class TransactionHistoryOrders(
    val currentPage: Int,
    val totalPages: Int,
    val records: List<TransactionHistoryOrder>,
)

data class TransactionHistoryOrder(
    val Description: String?,
    val RefID: String,
    val Action: String,
    val TransactionType: String,
    @JsonFormat(pattern = "dd/MM/yy HH:mm:ss")
    val TransactionDate: LocalDateTime,
    @JsonFormat(pattern = "dd/MM/yy HH:mm:ss")
    val OpenPeriod: LocalDateTime,
    val OpenPrice: Float=0f,
    val ClosePrice: Float=0f,
    val Amount: Float=0f,
    val Status: Int,
    val Message: String?,
)
