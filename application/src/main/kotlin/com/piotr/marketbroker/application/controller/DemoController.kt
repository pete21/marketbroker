package com.piotr.marketbroker.application.controller

import com.piotr.marketbroker.application.model.ResponseDTO
import com.piotr.marketbroker.application.model.SessionDTO
import com.piotr.marketbroker.application.service.TD365SessionService
import com.piotr.marketbroker.configuration.security.properties.SecurityRole
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import mu.KotlinLogging
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.RestController

private val log = KotlinLogging.logger(DemoController::class.toString())

@RestController
class DemoController(
    private val td365SessionService: TD365SessionService
): DemoApi {

    @PreAuthorize("hasRole('${SecurityRole.role_manager}')")
    @RequestMapping(
        method = [RequestMethod.POST],
        value = ["/demo/session"],
        produces = ["application/json"],
        consumes = ["application/json"]
    )
    override fun demoSession(sessionDTO: SessionDTO?): ResponseEntity<ResponseDTO> {
        log.info("Demo-session request: $sessionDTO")

        when (sessionDTO!!.state) {
            "START" -> {
                if (td365SessionService.demoSessionStart()) {
                        log.info("Session started")
                        return ResponseEntity<ResponseDTO>(ResponseDTO(0,"Session started"), HttpStatus.OK)
                    }
                log.warn("Session start error")
                return ResponseEntity<ResponseDTO>(ResponseDTO(1, "Session start error"), HttpStatus.OK)
            }

            "STOP" -> {
                td365SessionService.sessionStop()
                log.info("Session stopped")
                return ResponseEntity<ResponseDTO>(ResponseDTO(0, "Session stopped"), HttpStatus.OK)
            }
        }
        return ResponseEntity<ResponseDTO>(ResponseDTO(1, "Invalid command"), HttpStatus.BAD_REQUEST)

    }

    @PreAuthorize("hasRole('${SecurityRole.role_manager}')")
    @RequestMapping(
        method = [RequestMethod.GET],
        value = ["/config"],
        produces = ["application/json"]
    )
    fun getConfig() : String {
        return td365SessionService.getTD365ConfigurationProperties()
    }

}
