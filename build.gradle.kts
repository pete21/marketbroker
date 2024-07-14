plugins {
    id("org.sonarqube") version "4.4.1.3373"
    kotlin("jvm") version "1.9.22"
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(project(":application"))

}
