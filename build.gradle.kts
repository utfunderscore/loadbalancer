import java.util.Properties

plugins {
    kotlin("jvm") version "2.1.0"
    id("com.gradleup.shadow") version "9.0.0-beta11"
}

group = "io.github.utfunderscore"
version = "1.0.0"

repositories {
    mavenCentral()
    maven {
        url = uri("https://mvn.utf.lol/releases")
    }
}

dependencies {
    testImplementation(kotlin("test"))
    implementation("net.minestom:scratch:1.0.0")
    implementation("net.minestom:minestom-snapshots:39d445482f")
    implementation("org.jctools:jctools-core:4.0.5")
    implementation("net.kyori:adventure-text-minimessage:4.19.0")
    implementation("com.jayway.jsonpath:json-path:2.9.0")
    implementation("com.michael-bull.kotlin-result:kotlin-result:2.0.1")

    // logging
    implementation("org.tinylog:tinylog-api-kotlin:2.7.0")
    implementation("org.tinylog:tinylog-impl:2.7.0")
    implementation("org.tinylog:slf4j-tinylog:2.7.0")
    implementation("io.github.oshai:kotlin-logging-jvm:7.0.3")

    // Config library
    implementation("com.sksamuel.hoplite:hoplite-yaml:2.9.0")
    implementation("com.sksamuel.hoplite:hoplite-watch:2.9.0")
}

tasks.shadowJar {
    dependsOn("createProperties")
    minimize()

    archiveBaseName = "loadbalancer"
    archiveClassifier = ""
    archiveVersion = ""
}

tasks.register("createProperties") {
    doLast {
        val propertiesFile = file("$buildDir/resources/main/version.properties")
        propertiesFile.parentFile.mkdirs()
        propertiesFile.writer().use { writer ->
            val properties = Properties()
            properties["version"] = project.version.toString()
            properties["buildTime"] = System.currentTimeMillis().toString()
            properties.store(writer, null)
        }
    }
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(23)
}
