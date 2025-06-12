import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    java
    kotlin("jvm") version "2.0.0"
    alias(libs.plugins.loom)
    id("maven-publish")
    alias(libs.plugins.ksp)
}

repositories {
    maven(url = "https://maven.teamresourceful.com/repository/maven-public/")
    maven(url = "https://repo.hypixel.net/repository/Hypixel/")
    maven(url = "https://api.modrinth.com/maven")
    maven(url = "https://pkgs.dev.azure.com/djtheredstoner/DevAuth/_packaging/public/maven/v1")
    maven(url = "https://maven.nucleoid.xyz")
    maven(url = "https://maven.shedaniel.me/")
    mavenCentral()
    mavenLocal()
}

dependencies {
    compileOnly(libs.meowdding.ktmodules)
    ksp(libs.meowdding.ktmodules)
    compileOnly(libs.meowdding.ktcodecs)
    ksp(libs.meowdding.ktcodecs)

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
    modImplementation(libs.placeholders)
    modImplementation(libs.meowdding.patches)

    modCompileOnly(libs.rconfig)

    modCompileOnly(libs.rei)

    include(libs.hypixelapi)
    include(libs.skyblockapi)
    include(libs.rlib)
    include(libs.olympus)
    include(libs.placeholders)
    include(libs.meowdding.patches)

    modCompileOnly(libs.rconfig)

    modRuntimeOnly(libs.devauth)
    modRuntimeOnly(libs.modmenu)
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

    withType<JavaCompile>().configureEach {
        options.encoding = "UTF-8"
        options.release.set(21)
    }
}

java {
    withSourcesJar()
}

ksp {
    arg("meowdding.modules.project_name", "MeowddingLib")
    arg("meowdding.modules.package", "me.owdding.lib.generated")
    arg("meowdding.codecs.project_name", "MeowddingLib")
    arg("meowdding.codecs.package", "me.owdding.lib.generated")
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            artifactId = "meowdding-lib-${libs.versions.minecraft.get()}"
            from(components["java"])

            pom {
                name.set("MeowddingLib")
                url.set("https://github.com/meowdding/meowdding-lib")

                scm {
                    connection.set("git:https://github.com/meowdding/meowdding-lib.git")
                    developerConnection.set("git:https://github.com/meowdding/meowdding-lib.git")
                    url.set("https://github.com/meowdding/meowdding-lib")
                }
            }
        }
    }
    repositories {
        maven {
            setUrl("https://maven.teamresourceful.com/repository/thatgravyboat/")
            credentials {
                username = System.getenv("MAVEN_USER") ?: providers.gradleProperty("maven_username").orNull
                password = System.getenv("MAVEN_PASS") ?: providers.gradleProperty("maven_password").orNull
            }
        }
    }
}
