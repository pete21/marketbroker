package com.piotr.marketbroker.infrastructure.rest.controller

import com.piotr.marketbroker.application.controller.SubscriptionsApi
import com.piotr.marketbroker.application.model.SubscriptionsRequestDTO
import com.piotr.marketbroker.application.service.SubscriptionsService
import com.piotr.marketbroker.configuration.security.properties.SecurityRole
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.RestController

@Tag(name="subscriptions")
@RestController
class SubscriptionsController(
private val subscriptionsService: SubscriptionsService
) : SubscriptionsApi {

    @PreAuthorize("hasRole('${SecurityRole.role_manager}')")
    override fun getSubscriptions(): ResponseEntity<List<String>> {
        return ResponseEntity<List<String>>(subscriptionsService.getSubscriptions(), HttpStatus.OK)

    }

    @PreAuthorize("hasRole('${SecurityRole.role_manager}')")
    override fun createSubscription(subscriptionsRequestDTO: SubscriptionsRequestDTO): ResponseEntity<Unit> {
        val result = subscriptionsService.postSubscriptions(
            subscriptionsRequestDTO.quoteId, subscriptionsRequestDTO.status)
        return if (result) {
            ResponseEntity.ok().build()
        } else {
            ResponseEntity.status(HttpStatus.BAD_REQUEST).build()
        }
    }

}
