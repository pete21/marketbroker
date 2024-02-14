package com.piotr.marketbroker.application.controller

import ai.symmetrical.kafka.producer.FixedTopicMessageProducer
import com.piotr.marketbroker.application.event.kafka.JsonDataKafkaEvent
import com.piotr.marketbroker.application.model.JsonDataDTO
import com.piotr.marketbroker.configuration.security.properties.SecurityRole
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RestController

@RestController
class PublishToKafkaController(
    private val producer: FixedTopicMessageProducer<JsonDataKafkaEvent>
): KafkaApi {

    @PreAuthorize("hasRole('${SecurityRole.role_manager}')")
    @RequestMapping(
        method = [RequestMethod.POST],
        value = ["/publish-json"],
        consumes = ["application/json"]
    )
   override fun publishJson(jsonDataDTO: JsonDataDTO?): ResponseEntity<Unit> {
        producer.produce(JsonDataKafkaEvent(jsonDataDTO!!.type, jsonDataDTO.data), null, null)
        return ResponseEntity.ok().build()
    }

}
