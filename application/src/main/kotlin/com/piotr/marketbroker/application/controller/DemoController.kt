package com.piotr.marketbroker.application.controller

import com.piotr.marketbroker.application.model.ResponseDTO
import com.piotr.marketbroker.application.model.SessionDTO
import com.piotr.marketbroker.application.service.TD365ApiService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import mu.KotlinLogging
import org.springframework.stereotype.Component

private val log = KotlinLogging.logger {}

@Component
class DemoController(
    private val td365ApiService: TD365ApiService
): DemoApi {
    @RequestMapping(
        method = [RequestMethod.POST],
        value = ["/demo"],
        produces = ["application/json"],
        consumes = ["application/json"]
    )
    override fun postDemo(sessionDTO: SessionDTO?): ResponseEntity<ResponseDTO> {
        log.info("Demo-session request: $sessionDTO")

        when (sessionDTO!!.state) {
            "START" -> {
                if (td365ApiService.demoSessionStart()) {
                        log.info("Session started")
                        return ResponseEntity<ResponseDTO>(ResponseDTO(0,"Session started"), HttpStatus.OK)
                    }
                log.warn("Session start error")
                return ResponseEntity<ResponseDTO>(ResponseDTO(1, "Session start error"), HttpStatus.OK)
            }

            "STOP" -> {
                td365ApiService.demoSessionStop()
                log.info("Session stopped")
                return ResponseEntity<ResponseDTO>(ResponseDTO(0, "Session stopped"), HttpStatus.OK)
            }
        }
        return ResponseEntity<ResponseDTO>(ResponseDTO(1, "Invalid command"), HttpStatus.BAD_REQUEST)

    }
}
