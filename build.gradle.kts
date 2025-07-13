@file:Suppress("UnstableApiUsage")

import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.dsl.KotlinVersion
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    java
    kotlin("jvm") version "2.0.0"
    alias(libs.plugins.terrarium.cloche)
    id("maven-publish")
    alias(libs.plugins.kotlin.symbol.processor)
}

repositories {
    maven(url = "https://maven.teamresourceful.com/repository/maven-public/")
    maven(url = "https://repo.hypixel.net/repository/Hypixel/")
    maven(url = "https://api.modrinth.com/maven")
    maven(url = "https://pkgs.dev.azure.com/djtheredstoner/DevAuth/_packaging/public/maven/v1")
    maven(url = "https://maven.nucleoid.xyz")
    maven(url = "https://maven.msrandom.net/repository/cloche")
    maven(url = "https://maven.msrandom.net/repository/root")
    maven(url = "https://maven.shedaniel.me/")
    mavenCentral()
    mavenLocal()
}

tasks.withType<KotlinCompile>().configureEach {
    compilerOptions {
        languageVersion = KotlinVersion.KOTLIN_2_0
        freeCompilerArgs.addAll(
            "-Xmulti-platform",
            "-Xno-check-actual",
            "-Xexpect-actual-classes",
        )
    }
}

dependencies {
    compileOnly(libs.meowdding.ktmodules)
    ksp(libs.meowdding.ktmodules)
    compileOnly(libs.meowdding.ktcodecs)
    ksp(libs.meowdding.ktcodecs)

    compileOnly(libs.kotlin.stdlib)
}

cloche {
    metadata {
        modId = "meowdding-lib"
        name = "Meowdding-Lib"
        license = ""
        clientOnly = true

        custom("modmenu" to mapOf("badges" to listOf("library")))
    }

    common {
        dependencies {
            compileOnly(libs.meowdding.ktcodecs)
            compileOnly(libs.meowdding.ktmodules)

            modImplementation(libs.hypixelapi)
            modImplementation(project.dependencies.variantOf(libs.skyblockapi) { artifactType("jar") })
            modImplementation(libs.resourceful.lib1215)
            modImplementation(libs.placeholders) { isTransitive = false }
            modImplementation(libs.meowdding.patches) { isTransitive = false }
            modImplementation(libs.resourceful.config) { isTransitive = false }

            modCompileOnly(libs.rei)

            modImplementation(libs.fabric.language.kotlin)
        }
    }

    fun createVersion(
        name: String,
        version: String = name,
        loaderVersion: Provider<String> = libs.versions.fabric.loader,
        fabricApiVersion: Provider<String> = libs.versions.fabric.api,
        dependencies: MutableMap<String, Provider<MinimalExternalModuleDependency>>.() -> Unit = { },
    ) {
        val dependencies = mutableMapOf<String, Provider<MinimalExternalModuleDependency>>().apply(dependencies)
        val rlib = dependencies["resourcefullib"]!!
        val olympus = dependencies["olympus"]!!

        fabric(name) {
            includedClient()
            minecraftVersion = version
            this.loaderVersion = loaderVersion.get()

            include(libs.hypixelapi)
            include(project.dependencies.variantOf(libs.skyblockapi) {
                classifier(version)
                artifactType("jar")
            })
            include(rlib)
            include(olympus)
            include(libs.placeholders)
            include(libs.meowdding.patches)

            metadata {
                entrypoint("client") {
                    adapter = "kotlin"
                    value = "me.owdding.lib.MeowddingLib"
                }
                entrypoint("rei_client") {
                    adapter = "kotlin"
                    value = "me.owdding.lib.compat.REICompatability"
                }

                fun dependency(modId: String, version: Provider<String>) {
                    dependency {
                        this.modId = modId
                        version {
                            this.start = version
                            this.startInclusive = true
                        }
                    }
                }

                dependency("fabricloader", libs.versions.fabric.loader)
                dependency("fabric-language-kotlin", libs.versions.fabric.language.kotlin)
//                 dependency("meowdding-patches", libs.versions.meowdding.patches)
//                 dependency("resourcefullib", libs.versions.rlib)
                dependency("skyblock-api", libs.versions.skyblockapi)
//                 dependency("olympus", libs.versions.olympus)
//                 dependency("placeholder-api", libs.versions.placeholders)
            }

            dependencies {
                fabricApi(fabricApiVersion, minecraftVersion)
                modImplementation(olympus)
            }

            runs {
                client()
            }
        }
    }

    createVersion("1.21.5", fabricApiVersion = provider { "0.127.1" }) {
        this["resourcefullib"] = libs.resourceful.lib1215
        this["olympus"] = libs.olympus.lib1215
    }
    createVersion("1.21.7") {
        this["resourcefullib"] = libs.resourceful.lib1217
        this["olympus"] = libs.olympus.lib1217
    }

    mappings { official() }
}

tasks {
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
    this@ksp.excludedSources.from(sourceSets.getByName("1215").kotlin.srcDirs)
    this@ksp.excludedSources.from(sourceSets.getByName("1217").kotlin.srcDirs)
    arg("meowdding.project_name", "MeowddingLib")
    arg("meowdding.package", "me.owdding.lib.generated")
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
