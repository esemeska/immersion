plugins {
    kotlin("jvm") version "2.2.0"
}

repositories {
    mavenCentral()
    maven {
        name = "papermc"
        url = uri("https://repo.papermc.io/repository/maven-public/")
    }
}

dependencies {
    compileOnly("io.papermc.paper:paper-api:1.21.1-R0.1-SNAPSHOT")
    compileOnly("net.kyori:adventure-api:4.22.0")
    compileOnly("net.kyori:adventure-text-serializer-plain:4.22.0")

    compileOnly("org.jetbrains.exposed:exposed-jdbc:1.0.0-beta-4")
    compileOnly("com.zaxxer:HikariCP:4.0.3")

    compileOnly("com.github.shynixn.mccoroutine:mccoroutine-bukkit-api:2.22.0")
    compileOnly("com.github.shynixn.mccoroutine:mccoroutine-bukkit-core:2.22.0")
    compileOnly("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.10.2")
}

tasks.withType<Test>().configureEach {
    enabled = false
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(21))
}