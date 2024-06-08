import org.springframework.boot.gradle.tasks.bundling.BootJar

plugins {
    alias(libs.plugins.org.springframework.boot)
    id("org.openapi.generator") version "7.5.0"
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-validation")
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
