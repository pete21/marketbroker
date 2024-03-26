package com.piotr.marketbroker.application.controller

import ai.symmetrical.kafka.producer.FixedTopicMessageProducer
import com.piotr.marketbroker.application.event.kafka.JsonDataKafkaEvent
import com.piotr.marketbroker.application.event.kafka.TextDataKafkaEvent
import com.piotr.marketbroker.application.model.JsonDataDTO
import com.piotr.marketbroker.application.model.TextDataDTO
import com.piotr.marketbroker.configuration.security.properties.SecurityRole
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RestController

@RestController
class PublishToKafkaController(
    private val jsonProducer: FixedTopicMessageProducer<JsonDataKafkaEvent>,
    private val textProducer: FixedTopicMessageProducer<TextDataKafkaEvent>
): KafkaApi {

    @Tag(name="kafka")
    @PreAuthorize("hasRole('${SecurityRole.role_manager}')")
    @RequestMapping(
        method = [RequestMethod.POST],
        value = ["/publish-json"],
        consumes = ["application/json"]
    )
   override fun publishJson(jsonDataDTO: JsonDataDTO): ResponseEntity<Unit> {
        jsonProducer.produce(JsonDataKafkaEvent(jsonDataDTO.type, jsonDataDTO.data), null, null)
        return ResponseEntity.ok().build()
    }

    @Tag(name="kafka")
    @PreAuthorize("hasRole('${SecurityRole.role_manager}')")
    @RequestMapping(
        method = [RequestMethod.POST],
        value = ["/publish-text"],
        consumes = ["application/json"]
    )
    override fun publishText(textDataDTO: TextDataDTO): ResponseEntity<Unit> {
        textProducer.produce(TextDataKafkaEvent(textDataDTO.message), null, null)
        return ResponseEntity.ok().build()
    }

}
