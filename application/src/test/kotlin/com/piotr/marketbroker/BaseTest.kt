package com.piotr.marketbroker

import com.piotr.marketbroker.configuration.TestMarketbrokerApplicationConfiguration
import com.piotr.marketbroker.initializers.KafkaTestContainerInitializer
import io.kotest.core.spec.style.ExpectSpec
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock
import org.springframework.context.annotation.Import
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration

@SpringBootTest
@ActiveProfiles("integration-test")
@ContextConfiguration(initializers = [KafkaTestContainerInitializer::class])
@Import(TestMarketbrokerApplicationConfiguration::class)
@AutoConfigureWireMock(port = 0)
class BaseTest(body: ExpectSpec.() -> Unit = {}) : ExpectSpec(body) {

//    @Autowired
//    private lateinit var repositories: Collection<MongoRepository<*, *>>
//
//    override suspend fun afterEach(testCase: TestCase, result: TestResult) {
//        repositories.forEach { it.deleteAll() }
//    }
}
