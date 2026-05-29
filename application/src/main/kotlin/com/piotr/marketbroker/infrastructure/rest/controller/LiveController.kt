package com.piotr.marketbroker.infrastructure.rest.controller

import com.piotr.marketbroker.application.controller.LiveApi
import com.piotr.marketbroker.application.model.LiveAccountDTO
import com.piotr.marketbroker.application.model.LoginDTO
import com.piotr.marketbroker.application.model.ResponseDTO
import com.piotr.marketbroker.application.model.SessionDTO
import com.piotr.marketbroker.application.service.TD365SessionService
import com.piotr.marketbroker.configuration.security.properties.SecurityRole
import io.swagger.v3.oas.annotations.tags.Tag
import com.piotr.marketbroker.common.logger
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.RestController

@Tag(name="live")
@RestController
class LiveController(
    private val td365SessionService: TD365SessionService
): LiveApi {

    private val log by logger()

    @PreAuthorize("hasRole('${SecurityRole.role_manager}')")
    override fun liveLogin(loginDTO: LoginDTO): ResponseEntity<ResponseDTO> {
        log.info("liveLogin request: $loginDTO")
        when (loginDTO.state) {
            "START" -> {
                if (td365SessionService.liveLogin()) {
                    log.info("liveLogin OK")
                    return ResponseEntity<ResponseDTO>(ResponseDTO(0, "Login OK"), HttpStatus.OK)
                }
                log.warn("liveLogin error")
                return ResponseEntity<ResponseDTO>(ResponseDTO(1, "Login error"), HttpStatus.OK)
            }
            "STOP" -> {
                td365SessionService.liveLogout()
                return ResponseEntity<ResponseDTO>(ResponseDTO(0, "Logout OK"), HttpStatus.OK)
            }
        }
        return ResponseEntity<ResponseDTO>(ResponseDTO(1, "Unrecognized request"), HttpStatus.BAD_REQUEST)
    }

    @PreAuthorize("hasRole('${SecurityRole.role_manager}')")
    override fun getLiveAccounts(): ResponseEntity<List<LiveAccountDTO>> {
        val accounts = td365SessionService.getAccounts()?.app_metadata?.trading_accounts?.map { LiveAccountDTO(
            it?.id ?: 0, it?.platform ?:"", it?.account_id ?:"", it?.type ?:"", it?.currency ?: "",
            it?.balance?.cash_balance?.toFloat() ?:0f, it?.balance?.total_equity?.toFloat() ?:0f,
            it?.ct_login_id, it?.ct_login_password) }.orEmpty()
        return ResponseEntity<List<LiveAccountDTO>>(accounts, HttpStatus.OK)
    }

    @PreAuthorize("hasRole('${SecurityRole.role_manager}')")
    override fun liveSession(sessionDTO: SessionDTO): ResponseEntity<ResponseDTO> {
        log.info("liveSession request: $sessionDTO")

        when (sessionDTO.state) {
            "START" -> {
                if (td365SessionService.liveSessionStart(sessionDTO.accountId!!)) {
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
        return ResponseEntity<ResponseDTO>(ResponseDTO(1, "Unrecognized request"), HttpStatus.BAD_REQUEST)

    }

}
