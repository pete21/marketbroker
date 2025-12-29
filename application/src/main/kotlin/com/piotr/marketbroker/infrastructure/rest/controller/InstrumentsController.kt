package com.piotr.marketbroker.infrastructure.rest.controller

import com.piotr.marketbroker.application.controller.InstrumentsApi
import com.piotr.marketbroker.application.model.TickResponseDTO
import com.piotr.marketbroker.application.service.MarketGroupService
import com.piotr.marketbroker.application.service.MarketQuoteService
import com.piotr.marketbroker.configuration.security.properties.SecurityRole
import io.swagger.v3.oas.annotations.tags.Tag
import com.piotr.marketbroker.common.logger
import com.piotr.marketbroker.domain.tick.port.TickState
import com.piotr.marketbroker.infrastructure.rest.controller.mapper.TickMapper
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.RestController

@Tag(name="instruments")
@RestController
class InstrumentsController(
    private val marketGroupService: MarketGroupService,
    private val marketQuoteService: MarketQuoteService,
    private val tickState: TickState
): InstrumentsApi {

    private val log by logger()

    @PreAuthorize("hasRole('${SecurityRole.role_manager}')")
    override fun getInstrumentGroups(): ResponseEntity<List<String>> {
        log.info("getInstrumentGroups request")
        return ResponseEntity<List<String>>(marketGroupService.getInstrumentGroups(), HttpStatus.OK)
    }

    @PreAuthorize("hasRole('${SecurityRole.role_manager}')")
    override fun getInstrumentQuotes(): ResponseEntity<List<String>> {
        log.info("getInstrumentQuotes request")
        return ResponseEntity<List<String>>(marketQuoteService.getInstrumentQuotes(), HttpStatus.OK)
    }

    @PreAuthorize("hasRole('${SecurityRole.role_manager}')")
    override fun postInstrumentGroups(): ResponseEntity<Unit> {
        marketGroupService.postInstrumentGroups()
        return ResponseEntity.ok().build()
    }

    @PreAuthorize("hasRole('${SecurityRole.role_manager}')")
    override fun postInstrumentQuotes(): ResponseEntity<Unit> {
        marketQuoteService.postInstrumentQuotes()
        return ResponseEntity.ok().build()
    }

    @PreAuthorize("hasRole('${SecurityRole.role_manager}')")
    override fun getInstrumentTicks(): ResponseEntity<List<TickResponseDTO>> {
        val tickResponse =  tickState.getAll().map { TickMapper.mapTickToTickResponseDto(it) }
        return ResponseEntity(tickResponse, HttpStatus.OK)
    }

}
