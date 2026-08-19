plugins {
    java
    id("io.papermc.paperweight.userdev") version "1.7.1"
    id("xyz.jpenilla.run-paper") version "2.3.1"
}

group = "com.twicefear"
version = "1.0.0"
description = "Diablo SMP - 15 Powerful Diablo Stones Plugin"

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(21))
}

repositories {
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public/")
}

dependencies {
    paperweight.paperDevBundle("1.21.1-R0.1-SNAPSHOT")
    compileOnly("io.papermc.paper:paper-api:1.21.1-R0.1-SNAPSHOT")
}

tasks {
    compileJava {
        options.encoding = Charsets.UTF_8.name()
        options.release.set(21)
    }
    processResources {
        val props = mapOf("version" to version)
        filesMatching("plugin.yml") {
            expand(props)
        }
    }
    runServer {
        minecraftVersion("1.21.1")
    }
}
