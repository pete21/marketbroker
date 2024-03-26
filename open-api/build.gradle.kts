import org.springframework.boot.gradle.tasks.bundling.BootJar

plugins {
    alias(libs.plugins.org.springframework.boot)
    kotlin("jvm") version libs.versions.org.jetbrains.kotlin
    kotlin("plugin.spring") version libs.versions.org.jetbrains.kotlin
    id("org.openapi.generator") version "7.4.0"
}


dependencies {
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-validation")

    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
}

tasks.withType<Test> {
    useJUnitPlatform()
}

tasks.getByName<BootJar>("bootJar") {
    enabled = false
}

tasks.getByName<Jar>("jar") {
    enabled = true
}

// Validating a single specification
openApiValidate {
    inputSpec.set("$projectDir/src/main/resources/marketbroker.yaml")
}

openApiGenerate {
    generatorName.set("kotlin-spring")
    inputSpec.set("$projectDir/src/main/resources/marketbroker.yaml")
    outputDir.set("${layout.buildDirectory}/generated")
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
            File("$layout.buildDirectory/generated").deleteRecursively()
        }
    }

    clean { dependsOn("cleanGeneratedCodeTask") }
    compileJava { dependsOn(openApiGenerate) }
    compileKotlin { dependsOn(openApiGenerate) }
}

sourceSets[SourceSet.MAIN_SOURCE_SET_NAME].java {
    srcDir(
        "$layout.buildDirectory/generated/src/main/kotlin"
    )
}
