package com.piotr.marketbroker.application.controller

import com.piotr.marketbroker.application.model.LiveAccountDTO
import com.piotr.marketbroker.application.model.LoginDTO
import com.piotr.marketbroker.application.model.ResponseDTO
import com.piotr.marketbroker.application.model.SessionDTO
import com.piotr.marketbroker.application.service.TD365SessionService
import com.piotr.marketbroker.configuration.security.properties.SecurityRole
import mu.KotlinLogging
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RestController

private val log = KotlinLogging.logger(LiveController::class.toString())
@RestController
class LiveController(
    private val td365SessionService: TD365SessionService
): LiveApi {

    @PreAuthorize("hasRole('${SecurityRole.role_manager}')")
    @RequestMapping(
        method = [RequestMethod.POST],
        value = ["/live/login"],
        produces = ["application/json"],
        consumes = ["application/json"]
    )
    override fun liveLogin(loginDTO: LoginDTO): ResponseEntity<ResponseDTO> {
        log.info("liveLogin request: $loginDTO")
        when (loginDTO.state) {
            "START" -> {
                if (td365SessionService.liveLogin()) {
                    log.info("liveLogin OK")
                    return ResponseEntity<ResponseDTO>(ResponseDTO(0, "Login OK"), HttpStatus.OK)
                }
                log.warn("liveLogin error")
                return ResponseEntity<ResponseDTO>(ResponseDTO(1, "liveLogin error"), HttpStatus.OK)
            }
            "STOP" -> {
                td365SessionService.liveLogout()
                return ResponseEntity<ResponseDTO>(ResponseDTO(0, "Logout OK"), HttpStatus.OK)
            }
        }
        return ResponseEntity<ResponseDTO>(ResponseDTO(1, "Bad request"), HttpStatus.BAD_REQUEST)
    }

    @PreAuthorize("hasRole('${SecurityRole.role_manager}')")
    @RequestMapping(
        method = [RequestMethod.GET],
        value = ["/live/accounts"],
        produces = ["application/json"]
    )
    override fun getLiveAccounts(): ResponseEntity<List<LiveAccountDTO>> {
        val accounts = td365SessionService.getAccounts()?.results?.map { LiveAccountDTO(
            it.id, it.platform, it.account, it.accountType, it.currency,
            it.balance.toFloat(), it.equity.toFloat(), it.ctLoginId, it.ctLoginPassword) }.orEmpty()
        return ResponseEntity<List<LiveAccountDTO>>(accounts, HttpStatus.OK)
    }

    @PreAuthorize("hasRole('${SecurityRole.role_manager}')")
    @RequestMapping(
        method = [RequestMethod.POST],
        value = ["/live/session"],
        produces = ["application/json"],
        consumes = ["application/json"]
    )
    override fun liveSession(sessionDTO: SessionDTO): ResponseEntity<ResponseDTO> {
        log.info("liveSession request: $sessionDTO")

        when (sessionDTO.state) {
            "START" -> {
                if (td365SessionService.liveSessionStart(sessionDTO!!.accountId!!)) {
                        log.info("liveSession started")
                        return ResponseEntity<ResponseDTO>(ResponseDTO(0, "Session started"), HttpStatus.OK)
                    }
                log.warn("liveSession start error")
                return ResponseEntity<ResponseDTO>(ResponseDTO(1, "Session start error"), HttpStatus.OK)
            }

            "STOP" -> {
                td365SessionService.sessionStop()
                log.info("liveSession stopped")
                return ResponseEntity<ResponseDTO>(ResponseDTO(0, "Session stopped"), HttpStatus.OK)
            }
        }
        return ResponseEntity<ResponseDTO>(ResponseDTO(1, "Invalid command"), HttpStatus.BAD_REQUEST)

    }

}
