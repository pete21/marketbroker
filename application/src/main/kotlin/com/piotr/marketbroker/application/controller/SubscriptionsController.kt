package com.piotr.marketbroker.application.controller

import com.piotr.marketbroker.application.model.SubscriptionsRequestDTO
import com.piotr.marketbroker.application.service.SubscriptionsService
import com.piotr.marketbroker.configuration.security.properties.SecurityRole
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RestController

@RestController
class SubscriptionsController(
private val subscriptionsService: SubscriptionsService
) : SubscriptionsApi {

    @PreAuthorize("hasRole('${SecurityRole.role_manager}')")
    @RequestMapping(
        method = [RequestMethod.GET],
        value = ["/instruments/subscriptions"],
        produces = ["application/json"]
    )
    override fun getInstrumentSubscriptions(): ResponseEntity<List<String>> {
        return ResponseEntity<List<String>>(subscriptionsService.getSubscriptions(), HttpStatus.OK)

    }

    @PreAuthorize("hasRole('${SecurityRole.role_manager}')")
    @RequestMapping(
        method = [RequestMethod.POST],
        value = ["/instruments/subscriptions"],
        produces = ["application/json"],
        consumes = ["application/json"]
    )
    override fun postInstrumentSubscriptions(subscriptionsRequestDTO: SubscriptionsRequestDTO?): ResponseEntity<Unit> {
        val result = subscriptionsService.postSubscriptions(
            subscriptionsRequestDTO!!.quoteId, subscriptionsRequestDTO!!.status)
        return if (result) {
            ResponseEntity.ok().build()
        } else {
            ResponseEntity.status(HttpStatus.BAD_REQUEST).build()
        }
    }

}