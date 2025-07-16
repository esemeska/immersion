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
    maven ("https://repo.codemc.org/repository/maven-public/") {
        name = "codemc"
    }
}

dependencies {
    implementation(project(":immersion-api"))
    compileOnly(project(":immersion-litematica"))

    compileOnly("io.papermc.paper:paper-api:1.21.1-R0.1-SNAPSHOT")

    //Discord, Networking
    implementation("com.discord4j:discord4j-core:3.2.8")

    //Redis
    implementation("io.lettuce:lettuce-core:6.7.1.RELEASE")

    //SkinsRestorer
    compileOnly("net.skinsrestorer:skinsrestorer-api:15.7.5")

    //Kotlin Coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor:1.10.2")

    // Exposed + R2DBC
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
    relocate("io.netty", "ua.senalll.discord.io.netty")

    relocate("reactor", "ua.senalll.discord.reactor")

    relocate("kotlinx.coroutines.reactor", "ua.senalll.discord.kotlinx.coroutines.reactor")

    relocate("io.lettuce", "ua.senalll.discord.io.lettuce")

    relocate("discord4j", "ua.senalll.discord.discord4j")

    relocate("org.jetbrains.exposed", "ua.senalll.discord.org.jetbrains.exposed")

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

