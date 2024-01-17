package com.piotr.marketbroker.configuration.rest.employer

import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.enums.ParameterIn


@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
@Parameter(
    `in` = ParameterIn.HEADER,
    name = "employerId",
    required = true,
    description = "Used by clients (services) to indicate employer context." +
            "Ignored for public APIs (end-users/bff)"
)
annotation class SetupEmployerContext
