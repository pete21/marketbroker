plugins {
    alias(libs.plugins.org.graalvm.buildtools.native)
    alias(libs.plugins.org.jetbrains.kotlin.jvm)

    alias(libs.plugins.org.jetbrains.kotlin.plugin.allopen)
    alias(libs.plugins.org.jetbrains.kotlin.plugin.spring)
    alias(libs.plugins.org.springframework.boot)

    alias(libs.plugins.com.github.ben.manes.versions)
    alias(libs.plugins.com.gorylenko.gradle.git.properties)
    alias(libs.plugins.org.springdoc.openapi.gradle.plugin)
}

springBoot {
    buildInfo()
}

tasks.bootJar {
    archiveFileName.set("app.jar")
    mainClass.set("com.piotr.marketbroker.MarketbrokerApplication")
}

tasks.bootRun {
    mainClass.set("com.piotr.marketbroker.MarketbrokerApplication")
}

dependencies {
    implementation(project(":open-api"))
    implementation(libs.ai.symmetrical.sym.kafka)
    implementation(libs.ai.symmetrical.sym.kafka.annotations)

    implementation(libs.org.springframework.cloud.spring.cloud.starter.openfeign)
    implementation(libs.org.springframework.boot.spring.boot.starter.actuator)
    implementation(libs.org.springframework.boot.spring.boot.starter.oauth2.resource.server)
    implementation(libs.org.springframework.boot.spring.boot.starter.security)
    implementation(libs.org.springframework.security.spring.security.oauth2.client)
    implementation(libs.org.springframework.boot.spring.boot.starter.validation)
    implementation(libs.org.springframework.boot.spring.boot.starter.web)
    implementation(libs.org.springdoc.springdoc.openapi.starter.webmvc.ui)

    implementation(libs.org.yaml.snakeyaml)
    implementation(libs.com.fasterxml.jackson.module.jackson.module.kotlin)

    implementation(libs.io.micrometer.micrometer.tracing.bridge.brave)
    implementation(libs.io.zipkin.reporter2.zipkin.reporter.brave)

    implementation(libs.jakarta.validation.api)

    implementation(libs.org.jetbrains.kotlin.kotlin.reflect)
    implementation(libs.org.jetbrains.kotlinx.kotlinx.coroutines.reactor)

    implementation(libs.io.github.microutils.kotlin.logging.jvm)

    runtimeOnly(libs.io.micrometer.micrometer.registry.prometheus)
    runtimeOnly(libs.org.jetbrains.kotlin.kotlin.reflect)

    testImplementation(libs.org.springframework.boot.spring.boot.starter.test)
    testImplementation(libs.org.springframework.boot.spring.boot.testcontainers)
    testImplementation(libs.org.springframework.security.test)

    testImplementation(libs.org.testcontainers.kafka)

    testImplementation(libs.com.tngtech.archunit.archunit.junit5)
    testImplementation(libs.com.tngtech.archunit.archunit.junit5.api)
    testImplementation(libs.com.tngtech.archunit.archunit.junit5.api)

    testImplementation(libs.io.kotest.kotest.runner.junit5)
    testImplementation(libs.io.kotest.kotest.assertions.core)
    testImplementation(libs.io.kotest.kotest.property)
    testImplementation(libs.io.kotest.extensions.kotest.extensions.spring)
    testImplementation(libs.io.kotest.kotest.framework.datatest)
    testImplementation(libs.org.danilopianini.khttp)
    testImplementation(libs.net.datafaker.datafaker)
    testImplementation(libs.org.springframework.cloud.spring.cloud.contract.wiremock)


    // https://mvnrepository.com/artifact/org.springframework.boot/spring-boot-devtools
    runtimeOnly("org.springframework.boot:spring-boot-devtools:3.2.0")
    // https://mvnrepository.com/artifact/org.springframework.boot/spring-boot-starter-data-jpa
    implementation("org.springframework.boot:spring-boot-starter-data-jpa:3.2.0")
    // https://mvnrepository.com/artifact/com.h2database/h2
    runtimeOnly("com.h2database:h2:2.2.224")
    // https://mvnrepository.com/artifact/org.springframework/spring-websocket
    implementation("org.springframework:spring-websocket:6.1.2")

//Observability
    // https://mvnrepository.com/artifact/com.github.loki4j/loki-logback-appender
    implementation("com.github.loki4j:loki-logback-appender:1.4.2")

    // https://mvnrepository.com/artifact/org.apache.httpcomponents/httpclient
    implementation("org.apache.httpcomponents:httpclient:4.5")

}

tasks.processAot.configure {
    environment("SPRING_SECURITY_OAUTH2_RESOURCESERVER_JWT_ISSUER_URI", "https://fake/auth/realms/mobile")
}

gitProperties {
    branch = System.getenv("BITBUCKET_BRANCH") ?: System.getenv("BITBUCKET_TAG")
}

tasks.withType<Test>().configureEach {
    useJUnitPlatform()
}

openApi {
    customBootRun {
        apiDocsUrl.set("http://localhost:8080/v3/api-docs.yaml")
        outputFileName.set("rest-contract.yaml")
        args.set(listOf("--spring.profiles.active=openapi"))
    }
}


configurations {
    runtimeOnly {
        exclude(group = "commons-logging", module = "commons-logging")
    }
}
