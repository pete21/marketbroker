package com.piotr.marketbroker.configuration.micrometer

import io.micrometer.common.KeyValue
import io.micrometer.observation.Observation
import io.micrometer.observation.ObservationHandler
import mu.KotlinLogging
import java.util.stream.StreamSupport

val log = KotlinLogging.logger(ObservationLogsHandler::class.toString())

class ObservationLogsHandler() : ObservationHandler<Observation.Context> {

    override fun onStart(context: Observation.Context) {
        log.info("Before running the observation for context [{}], userType [{}]", context.getName(), getTypeFromContext(context));
    }

    override fun onStop(context: Observation.Context) {
        log.info("After running the observation for context [{}], userType [{}]", context.getName(), getTypeFromContext(context));
    }

    override fun supportsContext(context: Observation.Context): Boolean {
        return true
    }

    private fun getTypeFromContext(context: Observation.Context): String =
        StreamSupport.stream(context.lowCardinalityKeyValues.spliterator(), false)
            .filter { keyValue -> "type" == keyValue.key }
        .map(KeyValue::getValue)
        .findFirst()
        .orElse("UNKNOWN")
}