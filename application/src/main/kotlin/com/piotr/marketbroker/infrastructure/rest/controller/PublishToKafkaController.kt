package com.piotr.marketbroker.infrastructure.rest.controller

import ai.symmetrical.kafka.producer.FixedTopicMessageProducer
import com.piotr.marketbroker.application.controller.KafkaApi
import com.piotr.marketbroker.application.event.kafka.JsonDataEvent
import com.piotr.marketbroker.application.event.kafka.TextDataEvent
import com.piotr.marketbroker.application.model.JsonDataDTO
import com.piotr.marketbroker.application.model.TextDataDTO
import com.piotr.marketbroker.configuration.security.properties.SecurityRole
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.RestController

@Tag(name="kafka")
@RestController
class PublishToKafkaController(
    private val jsonProducer: FixedTopicMessageProducer<JsonDataEvent>,
    private val textProducer: FixedTopicMessageProducer<TextDataEvent>
): KafkaApi {

    @PreAuthorize("hasRole('${SecurityRole.role_manager}')")
   override fun publishJson(jsonDataDTO: JsonDataDTO): ResponseEntity<Unit> {
        jsonProducer.produce(JsonDataEvent(jsonDataDTO.type, jsonDataDTO.data), null, null)
        return ResponseEntity.ok().build()
    }

    @PreAuthorize("hasRole('${SecurityRole.role_manager}')")
    override fun publishText(textDataDTO: TextDataDTO): ResponseEntity<Unit> {
        textProducer.produce(TextDataEvent(textDataDTO.message), null, null)
        return ResponseEntity.ok().build()
    }

}
