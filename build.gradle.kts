import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    java
    kotlin("jvm") version "2.0.0"
    alias(libs.plugins.loom)
}

repositories {
    maven(url = "https://maven.teamresourceful.com/repository/maven-public/")
    maven(url = "https://repo.hypixel.net/repository/Hypixel/")
    maven(url = "https://api.modrinth.com/maven")
    maven(url = "https://pkgs.dev.azure.com/djtheredstoner/DevAuth/_packaging/public/maven/v1")
    mavenCentral()
    mavenLocal()
}

dependencies {
    minecraft(libs.minecraft)
    mappings(loom.layered {
        officialMojangMappings()
        parchment("org.parchmentmc.data:parchment-1.21.3:2024.12.07@zip")
    })
    modImplementation(libs.loader)
    modImplementation(libs.fabrickotlin)
    modImplementation(libs.fabric)

    modImplementation(libs.hypixelapi)
    modImplementation(libs.skyblockapi)
    modImplementation(libs.rlib)
    modImplementation(libs.olympus)

    include(libs.hypixelapi)
    include(libs.skyblockapi)
    include(libs.rlib)
    include(libs.olympus)

    modRuntimeOnly(libs.devauth)
    modRuntimeOnly(libs.modmenu)
}

loom {
    runs {
        getByName("client") {
            property("devauth.configDir", rootProject.file(".devauth").absolutePath)
        }
    }
}

tasks {
    processResources {
        inputs.property("version", project.version)
        filesMatching("fabric.mod.json") {
            expand(getProperties())
            expand(mutableMapOf("version" to project.version))
        }
    }

    jar {
        from("LICENSE")
    }

    compileKotlin {
        compilerOptions {
            jvmTarget = JvmTarget.JVM_21
        }
    }

}

java {
    withSourcesJar()
}
