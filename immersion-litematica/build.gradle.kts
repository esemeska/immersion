import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    kotlin("jvm") version "2.2.0"
    id("com.gradleup.shadow") version "8.3.8"
}

group = "ua.senalll"
version = "0.1"

repositories {
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public/") {
        name = "papermc-repo"
    }
    maven("https://oss.sonatype.org/content/groups/public/") {
        name = "sonatype"
    }
    maven ("https://jitpack.io/"){

    }
}

dependencies {
    implementation(project(":immersion-api"))

    compileOnly("io.papermc.paper:paper-api:1.21.1-R0.1-SNAPSHOT")
    implementation("org.jetbrains.kotlin:kotlin-stdlib:2.2.0")

    implementation("com.fasterxml.jackson.core:jackson-databind:2.7.4")
    implementation("com.fasterxml.jackson.core:jackson-core:2.17.0")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.19.1")
    implementation("com.github.Querz:NBT:6.1")

    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.10.2")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor:1.10.2")

    implementation("org.jetbrains.exposed:exposed-core:1.0.0-beta-4")
    implementation("org.jetbrains.exposed:exposed-dao:1.0.0-beta-4")
    implementation("org.jetbrains.exposed:exposed-jdbc:1.0.0-beta-4")
    implementation("com.zaxxer:HikariCP:4.0.3")
    implementation("org.xerial:sqlite-jdbc:3.49.1.0")

    implementation(platform("io.projectreactor:reactor-bom:2024.0.0"))

    implementation("com.github.shynixn.mccoroutine:mccoroutine-bukkit-api:2.22.0")
    implementation("com.github.shynixn.mccoroutine:mccoroutine-bukkit-core:2.22.0")
}

tasks.named<ShadowJar>("shadowJar") {
    // Jackson
    relocate("com.fasterxml.jackson.core", "ua.senalll.litematica.com.fasterxml.jackson.core")
    relocate("com.fasterxml.jackson.databind", "ua.senalll.litematica.com.fasterxml.jackson.databind")
    relocate("com.fasterxml.jackson.module.kotlin", "ua.senalll.litematica.com.fasterxml.jackson.module.kotlin")

    // Querz NBT
    relocate("net.querz", "ua.senalll.litematica.net.querz")

    // Kotlin Coroutines
    relocate("kotlinx.coroutines", "ua.senalll.litematica.kotlinx.coroutines")

    // Kotlin Stdlib — осторожно! только если она реально шадится
    relocate("kotlin", "ua.senalll.litematica.kotlin")

    // Exposed ORM
    relocate("org.jetbrains.exposed", "ua.senalll.litematica.org.jetbrains.exposed")

    // R2DBC MySQL
    relocate("io.asyncer.r2dbc.mysql", "ua.senalll.litematica.io.asyncer.r2dbc.mysql")

    // Reactor
    relocate("reactor", "ua.senalll.litematica.reactor")
    mergeServiceFiles()
}


val targetJavaVersion = 21
kotlin {
    jvmToolchain(targetJavaVersion)
}

tasks.build {
    dependsOn("shadowJar")
}

tasks.processResources {
    val props = mapOf("version" to version)
    inputs.properties(props)
    filteringCharset = "UTF-8"
    filesMatching("paper-plugin.yml") {
        expand(props)
    }
}
