package com.piotr.marketbroker.application.controller

import com.piotr.marketbroker.application.service.InstrumentsService
import com.piotr.marketbroker.configuration.security.properties.SecurityRole
import mu.KotlinLogging
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RestController

private val log = KotlinLogging.logger(InstrumentsController::class.toString())

@RestController
class InstrumentsController(
    private val instrumentsService: InstrumentsService
): InstrumentsApi {

    @PreAuthorize("hasRole('${SecurityRole.role_manager}')")
    @RequestMapping(
        method = [RequestMethod.GET],
        value = ["/instruments/groups"],
        produces = ["application/json"]
    )
    override fun getInstrumentGroups(): ResponseEntity<List<String>> {
        log.info("getInstrumentGroups request")
        return ResponseEntity<List<String>>(instrumentsService.getInstrumentGroups(), HttpStatus.OK)
    }

    @PreAuthorize("hasRole('${SecurityRole.role_manager}')")
    @RequestMapping(
        method = [RequestMethod.GET],
        value = ["/instruments/quotes"],
        produces = ["application/json"]
    )
    override fun getInstrumentQuotes(): ResponseEntity<List<String>> {
        log.info("getInstrumentQuotes request")
        return ResponseEntity<List<String>>(instrumentsService.getInstrumentQuotes(), HttpStatus.OK)
    }

    @PreAuthorize("hasRole('${SecurityRole.role_manager}')")
    @RequestMapping(
        method = [RequestMethod.POST],
        value = ["/instruments/groups"]
    )
    override fun postInstrumentGroups(): ResponseEntity<Unit> {
        instrumentsService.postInstrumentGroups()
        return ResponseEntity.ok().build()
    }

    @PreAuthorize("hasRole('${SecurityRole.role_manager}')")
    @RequestMapping(
        method = [RequestMethod.POST],
        value = ["/instruments/quotes"]
    )
    override fun postInstrumentQuotes(): ResponseEntity<Unit> {
        instrumentsService.postInstrumentQuotes()
        return ResponseEntity.ok().build()
    }

}
