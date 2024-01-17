package com.piotr.marketbroker.application.usecase.pagination

data class PaginatedResponse<T>(
    val limit: Int,
    val offset: Int,
    val isLastPage: Boolean,
    val data: List<T>
)
