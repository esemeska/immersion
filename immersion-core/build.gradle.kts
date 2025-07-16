plugins {
    kotlin("jvm") version "2.2.0"
    id("com.gradleup.shadow") version "8.3.0"
    id("xyz.jpenilla.run-paper") version "2.3.1"
}

group = "ua.senalll"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public/") {
        name = "papermc-repo"
    }
    maven("https://oss.sonatype.org/content/groups/public/") {
        name = "sonatype"
    }
    maven ("https://repo.dmulloy2.net/repository/public/" )
    maven ("https://repo.codemc.org/repository/maven-public/") {
        name = "codemc"
    }
}

dependencies {
    implementation(project(":immersion-api"))
    compileOnly("io.papermc.paper:paper-api:1.21.1-R0.1-SNAPSHOT")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")

    compileOnly("com.comphenix.protocol:ProtocolLib:5.3.0")
    compileOnly("net.skinsrestorer:skinsrestorer-api:15.7.5")

    implementation("org.jetbrains.exposed:exposed-core:1.0.0-beta-4")
    implementation("org.jetbrains.exposed:exposed-dao:1.0.0-beta-40")
    implementation("org.jetbrains.exposed:exposed-jdbc:1.0.0-beta-4")
    implementation("com.zaxxer:HikariCP:6.3.0")
    implementation("org.xerial:sqlite-jdbc:3.50.2.0")
}

tasks {
    build {
        dependsOn("shadowJar")
    }
    processResources {
        val props = mapOf("version" to version)
        inputs.properties(props)
        filteringCharset = "UTF-8"
        filesMatching("paper-plugin.yml") {
            expand(props)
        }
    }
}

kotlin {
    jvmToolchain(21)
}
