package com.piotr.marketbroker.configuration.rest.employer

import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RestControllerAdvice
import java.util.*


@RestControllerAdvice(annotations = [SetupEmployerContext::class])
class SetupEmployerContextControllerAdvice(
    private val employerContext: EmployerContext
) {

    @ModelAttribute
    fun setupEmployerContext(@RequestHeader(name = "employerId", required = true) employerId: UUID) {
        employerContext._employerId = employerId
    }
}
