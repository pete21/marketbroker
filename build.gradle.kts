import io.gitlab.arturbosch.detekt.Detekt
import io.spring.gradle.dependencymanagement.dsl.DependencyManagementExtension
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    alias(libs.plugins.org.jetbrains.kotlin.jvm)

    alias(libs.plugins.io.spring.dependency.management)
    alias(libs.plugins.com.github.ben.manes.versions)
    alias(libs.plugins.nl.littlerobots.version.catalog.update)
    alias(libs.plugins.io.gitlab.arturbosch.detekt)
    alias(libs.plugins.org.sonarqube)
    id("jacoco")
}

val nexusDomain: String by project
val nexusUser: String by project
val nexusPassword: String by project
val appVersion: String by project

configurations {
    compileOnly {
        extendsFrom(configurations.annotationProcessor.get())
    }
}


detekt {
    source.setFrom(
        "application/src/main/kotlin/"
    )
    config.setFrom("detekt.yml")
    buildUponDefaultConfig = true // preconfigure defaults
    allRules = false // activate all available (even unstable) rules.
}

tasks.withType<Detekt>().configureEach {
    reports {
        html.required.set(true) // observe findings in your browser with structure and code snippets
        xml.required.set(true) // checkstyle like format mainly for integrations like Jenkins
    }
}

tasks.detekt {
    isEnabled = false
}


allprojects {
    apply(plugin = "org.jetbrains.kotlin.jvm")
    apply(plugin = "io.spring.dependency-management")
    java {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }

    configure<DependencyManagementExtension> {
        imports {
            mavenBom("org.springframework.cloud:spring-cloud-dependencies:2022.0.3")
        }
    }

    group = "com.piotr.marketbroker"
    version = appVersion.ifEmpty { "0.1.0-SNAPSHOT" }

    repositories {
        mavenCentral()
        maven {
            url = uri("https://${nexusDomain}/repository/maven-releases")
            credentials {
                username = nexusUser
                password = nexusPassword
            }
        }
        maven {
            url = uri("https://${nexusDomain}/repository/maven-snapshots")
            credentials {
                username = nexusUser
                password = nexusPassword
            }
            mavenContent {
                snapshotsOnly()
            }
        }
    }

    tasks.withType<KotlinCompile> {
        kotlinOptions {
            freeCompilerArgs = listOf("-Xjsr305=strict", "-Xemit-jvm-type-annotations")
            jvmTarget = "21"
        }
    }

    tasks.withType<Test> {
        jvmArgs = listOf("-XX:MaxMetaspaceSize=256m", "-Xmx1024m")
        useJUnitPlatform()
        ignoreFailures = (findProperty("test.ignoreFailures") as? String).toBoolean()
        System.getProperty("com.piotr.testcontainers.kafka.enabled")?.let {
            systemProperty("com.piotr.testcontainers.kafka.enabled", it)
        }
        System.getProperty("spring.kafka.bootstrap-servers")?.let {
            systemProperty("spring.kafka.bootstrap-servers", it)
        }
    }
}

dependencies {
    implementation(project(":application"))
    implementation(project(":open-api"))
}

copy {
    from("$rootDir/development/commit-msg")
    into("$rootDir/.git/hooks")
    fileMode = "777".toInt(8)
}

//sonarqube {
//    properties {
//        property("sonar.projectKey", "marketbroker")
//    }
//}

/*
tasks.getByName<BootJar>("bootJar") {
    mainClass.set("com.piotr.marketbroker.MarketbrokerApplication")
}

tasks.getByName<BootRun>("bootRun") {
    mainClass.set("com.piotr.marketbroker.MarketbrokerApplication")
}

tasks.bootJar {
    archiveFileName.set("app.jar")
    mainClass.set("com.piotr.marketbroker.MarketbrokerApplication")
}

tasks.bootRun {
    mainClass.set("com.piotr.marketbroker.MarketbrokerApplication")
}

 */
