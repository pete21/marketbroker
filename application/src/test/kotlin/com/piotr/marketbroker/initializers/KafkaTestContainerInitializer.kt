package com.piotr.marketbroker.initializers

import mu.KotlinLogging
import org.springframework.boot.test.util.TestPropertyValues
import org.springframework.context.ApplicationContextInitializer
import org.springframework.context.ConfigurableApplicationContext
import org.testcontainers.containers.KafkaContainer
import org.testcontainers.utility.DockerImageName

private val logger = KotlinLogging.logger { }

class KafkaTestContainerInitializer : ApplicationContextInitializer<ConfigurableApplicationContext> {

    val kafka = KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka:7.1.2"))

    override fun initialize(applicationContext: ConfigurableApplicationContext) {
        if (applicationContext.environment.getProperty("com.piotr.testcontainers.kafka.enabled") != "true") {
            logger.info("Kafka test container initialization skipped due to configuration")
            return
        }

        kafka.withReuse(false)
        kafka.start()

        val values = TestPropertyValues.of(
            "spring.kafka.bootstrap-servers=" + kafka.bootstrapServers
        )

        values.applyTo(applicationContext)
    }
}