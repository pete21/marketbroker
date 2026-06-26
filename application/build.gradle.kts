import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("org.springframework.boot") version "3.2.3"
    id("io.spring.dependency-management") version "1.1.4"
    id("com.gorylenko.gradle-git-properties") version "2.4.1"
    id("org.openapi.generator") version "7.6.0"
    id("org.graalvm.buildtools.native") version "0.10.1"
    kotlin("jvm")
    kotlin("plugin.spring") version "1.9.22"
    jacoco
}

val springFeignVersion: String by project
val kotestVersion: String by project
val archunitVersion: String by project
val mongockVersion: String by project

group = "com.piotr"

java {
    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21
}

springBoot {
    buildInfo()
}

dependencies {

    // spring boot libs
//    implementation("org.springframework.boot:spring-boot-starter-data-mongodb")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.boot:spring-boot-starter-oauth2-resource-server")
    implementation("org.springframework.boot:spring-boot-starter-oauth2-client")
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")

    // other spring libs
//    implementation("org.springframework.kafka:spring-kafka")
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.5.0")
    implementation("org.springframework.cloud:spring-cloud-starter-openfeign:$springFeignVersion")
    implementation("io.github.openfeign:feign-okhttp:13.3")


    // kotlin libs
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("org.jetbrains.kotlin:kotlin-reflect")

    // tracing
    implementation("io.micrometer:micrometer-registry-prometheus")
    implementation("io.micrometer:micrometer-tracing-bridge-otel")
    implementation("io.github.openfeign:feign-micrometer")
    implementation("io.opentelemetry:opentelemetry-exporter-zipkin")

    // symmetrical libs
    implementation("ai.symmetrical:json-log:1.1.0")
    implementation("ai.symmetrical:sym-cors:2.0.0")
    implementation("ai.symmetrical:sym-kafka:7.4.1")
    implementation("ai.symmetrical:sym-kafka-annotations:1.0.0")
//    implementation("ai.symmetrical:sym-request-response-logging:3.0.0")

    // database migrations
//    implementation("io.mongock:mongock-standalone:${mongockVersion}")
//    implementation("io.mongock:mongodb-sync-v4-driver:${mongockVersion}")

    // test libs
    testImplementation("io.kotest:kotest-runner-junit5:${kotestVersion}")
    testImplementation("io.kotest:kotest-assertions-core:${kotestVersion}")
    testImplementation("io.kotest:kotest-property:${kotestVersion}")
    testImplementation("io.kotest.extensions:kotest-extensions-spring:1.1.3")
    testImplementation("io.mockk:mockk:1.13.9")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("com.tngtech.archunit:archunit:${archunitVersion}")
    testImplementation("com.tngtech.archunit:archunit-junit5:${archunitVersion}")
    testImplementation("io.rest-assured:rest-assured:5.4.0")
    testImplementation("org.awaitility:awaitility-kotlin:4.2.1")

    // testcontainers
    testImplementation(platform("org.testcontainers:testcontainers-bom:1.19.7"))
    testImplementation("org.testcontainers:testcontainers")
//    testImplementation("org.testcontainers:mongodb")
    testImplementation("org.testcontainers:kafka")
    testImplementation("org.testcontainers:junit-jupiter")

// https://mvnrepository.com/artifact/com.mysql/mysql-connector-j
    implementation("com.mysql:mysql-connector-j:9.1.0")


    // https://mvnrepository.com/artifact/org.springframework.boot/spring-boot-devtools
//    runtimeOnly("org.springframework.boot:spring-boot-devtools:3.2.3")
    // https://mvnrepository.com/artifact/org.springframework.boot/spring-boot-starter-data-jpa
//    implementation("org.springframework.boot:spring-boot-starter-data-jpa:3.2.3")
    // https://mvnrepository.com/artifact/com.h2database/h2
//    runtimeOnly("com.h2database:h2:2.2.224")
    // https://mvnrepository.com/artifact/org.springframework/spring-websocket
    implementation("org.springframework:spring-websocket:6.1.2")
// https://mvnrepository.com/artifact/org.springframework/spring-messaging
    implementation("org.springframework:spring-messaging:6.1.2")


    //Observability
    // https://mvnrepository.com/artifact/com.github.loki4j/loki-logback-appender
    implementation("com.github.loki4j:loki-logback-appender:1.5.2")

    // https://mvnrepository.com/artifact/org.apache.httpcomponents.client5/httpclient5
    implementation("org.apache.httpcomponents.client5:httpclient5:5.3.1")

//    implementation("org.apache.logging.log4j:log4j-api:2.23.0")
//    implementation("org.apache.logging.log4j:log4j-core:2.23.0")

    // https://mvnrepository.com/artifact/com.fasterxml.jackson.datatype/jackson-datatype-jsr310
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310:2.16.1")

// https://mvnrepository.com/artifact/org.springframework.cloud/spring-cloud-contract-wiremock
    implementation("org.springframework.cloud:spring-cloud-contract-wiremock:4.1.4")


}
/*
tasks.processAot.configure {
    environment("SPRING_SECURITY_OAUTH2_RESOURCESERVER_JWT_ISSUER_URI", "https://fake/auth/realms/mobile")
}
*/

dependencyManagement {
    imports {
        mavenBom("org.springframework.cloud:spring-cloud-dependencies:2023.0.0")
    }
}


tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs += "-Xjsr305=strict"
        jvmTarget = "21"
    }
}

kotlin {
    jvmToolchain(21)
}

tasks.withType<Test> {
    useJUnitPlatform()
}

tasks.test {
    finalizedBy(tasks.jacocoTestReport) // report is always generated after tests run
}

tasks.jacocoTestReport {
    reports {
        xml.required.set(true)
    }
}

//openApi {
//    customBootRun {
//        apiDocsUrl.set("http://localhost:8090/v3/api-docs.yaml")
//        outputFileName.set("rest-contract.yaml")
//        args.set(listOf("--spring.profiles.active=openapi"))
//    }
//}

// Validating a single specification
openApiValidate {
    inputSpec.set("$projectDir/src/main/resources/marketbroker.yaml")
}

openApiGenerate {
    generatorName.set("kotlin-spring")
    inputSpec.set("$projectDir/src/main/resources/marketbroker.yaml")
    outputDir.set("${buildDir}/generated")
    configFile.set("$projectDir/src/main/resources/config.json")

    globalProperties.set(mapOf(
        Pair("apis", "Account,Demo,Instruments,Kafka,Live,Orders,Positions,Subscriptions"), //no value or comma-separated api names
        Pair("models", ""), //no value or comma-separated api names
    ))
}

tasks {
    register("cleanGeneratedCodeTask") {
        description = "Removes generated Open API code"

        doLast {
            File("${buildDir}/generated").deleteRecursively()
        }
    }

    clean { dependsOn("cleanGeneratedCodeTask") }
    compileJava { dependsOn(openApiGenerate) }
    compileKotlin { dependsOn(openApiGenerate) }
}

sourceSets[SourceSet.MAIN_SOURCE_SET_NAME].java {
    srcDir(
        "${buildDir}/generated/src/main/kotlin"
    )
}
