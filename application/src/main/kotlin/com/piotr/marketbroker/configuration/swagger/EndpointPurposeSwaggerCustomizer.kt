package com.piotr.marketbroker.configuration.swagger

import io.swagger.v3.oas.models.Operation
import org.springdoc.core.customizers.OperationCustomizer
import org.springframework.stereotype.Component
import org.springframework.web.method.HandlerMethod
@Component
class EndpointPurposeSwaggerCustomizer : OperationCustomizer {
    override fun customize(operation: Operation, handlerMethod: HandlerMethod?): Operation {
        if (operation.extensions == null) {
            operation.extensions = mutableMapOf<String, Any>()
        }
        handlerMethod?.getMethodAnnotation(InternalEndpoint::class.java)?.let {
            operation.extensions?.set("x-internal", true)
        }
        handlerMethod?.getMethodAnnotation(ExternalEndpoint::class.java)?.let {
            operation.extensions?.set("x-external", true)
        }
        return operation
    }
}
annotation class InternalEndpoint()
annotation class ExternalEndpoint()