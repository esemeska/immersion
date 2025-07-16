plugins {
    kotlin("jvm") version "2.2.0" apply false
}

allprojects {
    group = "ua.senalll"
    version = "0.1_alpha"

    repositories {
        mavenCentral()
        maven("https://repo.papermc.io/repository/maven-public/")
    }
}