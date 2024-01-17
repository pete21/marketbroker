package com.piotr.marketbroker.application.usecase.pagination

import org.springframework.data.domain.Page

data class PageDto<T>(
    val page: Int,
    val size: Int,
    val totalCount: Long,
    val content: Collection<T>
) {
    fun <V> copy(newContent: Collection<V>): PageDto<V> {
        return PageDto(page, size, totalCount, newContent)
    }

    companion object {
        fun <T> of(page: Page<T>): PageDto<T> = PageDto(
            page = page.number,
            size = page.size,
            totalCount = page.totalElements,
            content = page.content
        )
    }
}
