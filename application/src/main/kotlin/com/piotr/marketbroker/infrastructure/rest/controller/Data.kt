package com.piotr.marketbroker.infrastructure.rest.controller

import com.piotr.marketbroker.application.controller.DataApi
import com.piotr.marketbroker.application.model.HistoryResponseDTO
import com.piotr.marketbroker.configuration.security.properties.SecurityRole
import com.piotr.marketbroker.common.logger
import com.piotr.marketbroker.infrastructure.questdb.QuestDbAdapter
import com.piotr.marketbroker.infrastructure.rest.controller.mapper.HistoryMapper
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.RestController
import java.time.OffsetDateTime

@Tag(name="data")
@RestController
class DataController(
    private val questDbAdapter: QuestDbAdapter
): DataApi {

    private val log by logger()

    @PreAuthorize("hasRole('${SecurityRole.role_manager}')")
    override fun getHistory(ticker: String, period: String, start: OffsetDateTime?, end: OffsetDateTime?): ResponseEntity<HistoryResponseDTO> {
        log.info("getHistory request ticker=$ticker period=$period start=$start end=$end")
        val history = questDbAdapter.getDataHistory(
            ticker,
            period,
            start?.toString().orEmpty(),
            end?.toString().orEmpty(),
        )
        return ResponseEntity.ok(HistoryMapper.mapHistoryToHistoryResponseDto(history))
    }
}
