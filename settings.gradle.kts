rootProject.name = "marketbroker"

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.PREFER_SETTINGS)
    repositories {
        maven {
            name = "projectLocal"
            url = uri("${rootDir}/repository")
        }
        mavenCentral()
        mavenLocal()
    }
}

buildCache.local.directory = "/opt/gradle/caches/build-cache-${rootProject.name}"
include("application")
// include("open-api")

System.setProperty("sonar.gradle.skipCompile", "true")
