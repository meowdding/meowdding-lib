@file:Suppress("UnstableApiUsage")
@file:OptIn(ExperimentalPathApi::class)

import com.google.devtools.ksp.gradle.KspTask
import earth.terrarium.cloche.api.metadata.ModMetadata
import me.owdding.gradle.dependency
import net.msrandom.minecraftcodev.core.utils.toPath
import net.msrandom.stubs.GenerateStubApi
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.dsl.KotlinVersion
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import java.nio.file.StandardOpenOption
import java.util.zip.ZipFile
import kotlin.io.path.*

plugins {
    java
    kotlin("jvm") version "2.2.0"
    alias(libs.plugins.terrarium.cloche)
    id("maven-publish")
    alias(libs.plugins.kotlin.symbol.processor)
    id("me.owdding.gradle") version "1.1.1"
}

repositories {
    maven(url = "https://repo.hypixel.net/repository/Hypixel/")
    maven(url = "https://maven.msrandom.net/repository/cloche")
    maven(url = "https://maven.msrandom.net/repository/root")
    maven(url = "https://api.modrinth.com/maven")
    maven(url = "https://api.modrinth.com/maven")
    maven(url = "https://pkgs.dev.azure.com/djtheredstoner/DevAuth/_packaging/public/maven/v1")
    maven(url = "https://maven.teamresourceful.com/repository/maven-public/")
    maven(url = "https://maven.shedaniel.me/")
    mavenLocal()
}

tasks.withType<KotlinCompile>().configureEach {
    compilerOptions {
        languageVersion = KotlinVersion.KOTLIN_2_2
        freeCompilerArgs.addAll(
            "-Xmulti-platform",
            "-Xno-check-actual",
            "-Xexpect-actual-classes",
            "-Xopt-in=kotlin.time.ExperimentalTime",
        )
    }

}
val kspAll: Configuration by configurations.creating {
    isCanBeResolved = true
    isCanBeConsumed = true
}

dependencies {
    kspAll(libs.meowdding.ktmodules)
    kspAll(libs.meowdding.ktcodecs)
    kspAll("net.msrandom:kmp-actual-stubs-processor:1.0.5-meowwwwwwwwwwwwww") {
        version { strictly("1.0.5-meowwwwwwwwwwwwww") }
        isTransitive = false
    }

    compileOnly(libs.meowdding.ktmodules)
    compileOnly(libs.meowdding.ktcodecs)
    compileOnly(libs.kotlin.stdlib)
    configurations.forEach {
        if (it.name.startsWith("ksp") && !it.name.contains("classpath", true) && !it.name.contains("all", true)) {
            kspAll.allDependencies.forEach { dependency -> add(it.name, dependency) }
        }
    }
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
        withPublication()
        mixins.from("src/mixins/meowdding-lib.mixins.json")
        accessWideners.from("src/main/mlib.accesswidener")

        dependencies {
            compileOnly(libs.meowdding.ktcodecs)
            compileOnly(libs.meowdding.ktmodules)

            implementation(libs.hypixelapi) // included in skyblockapi
            implementation(libs.skyblockapi)
            implementation(libs.placeholders) { isTransitive = false }
            implementation(libs.resourceful.config.kotlin)

            compileOnly(libs.rei)

            implementation(libs.fabric.language.kotlin)
        }
    }

    fun createVersion(
        name: String,
        version: String = name,
        loaderVersion: Provider<String> = libs.versions.fabric.loader,
        fabricApiVersion: Provider<String> = libs.versions.fabric.api,
        endAtSameVersion: Boolean = true,
        minecraftVersionRange: ModMetadata.VersionRange.() -> Unit = {
            start = version
            if (endAtSameVersion) {
                end = version
                endExclusive = false
            }
        },
        dependencies: MutableMap<String, Provider<MinimalExternalModuleDependency>>.() -> Unit = { },
    ) {
        val dependencies = mutableMapOf<String, Provider<MinimalExternalModuleDependency>>().apply(dependencies)
        val rlib = dependencies["resourcefullib"]!!
        val rconfig = dependencies["resourcefulconfig"]!!
        val olympus = dependencies["olympus"]!!
        val iris = dependencies["iris"]!!

        fabric("versions:$name") {
            includedClient()
            minecraftVersion = version
            this.loaderVersion = loaderVersion.get()

            accessWideners.from(project.layout.projectDirectory.file("src/versions/$name/${name.replace(".", "")}.accesswidener"))

            mixins.from("src/mixins/meowdding-lib.${name.replace(".", "")}.mixins.json")

            metadata {
                entrypoint("client") {
                    adapter = "kotlin"
                    value = "me.owdding.lib.MeowddingLib"
                }
                entrypoint("rei_client") {
                    adapter = "kotlin"
                    value = "me.owdding.lib.compat.REICompatability"
                }

                dependency {
                    modId = "minecraft"
                    required = true
                    version(minecraftVersionRange)
                }
                dependency("fabric")
                dependency("fabricloader", libs.versions.fabric.loader)
                dependency("fabric-language-kotlin", libs.versions.fabric.language.kotlin)
                dependency("resourcefullib", rlib.map { it.version!! })
                dependency("olympus", olympus.map { it.version!! })
                dependency("skyblock-api", libs.versions.skyblockapi)
                dependency("placeholder-api", libs.versions.placeholders)

            }

            dependencies {
                fabricApi(fabricApiVersion, name)
                implementation(olympus)
                implementation(rlib)
                api(libs.meowdding.remote.repo)
                compileOnly(rconfig)
                localRuntime(rconfig)
                api(libs.keval)

                compileOnly(iris)

                include(libs.keval)
                include(rlib) { isTransitive = false }
                include(libs.meowdding.remote.repo)
                include(olympus) { isTransitive = false }
                include(libs.placeholders) { isTransitive = false }
                include(libs.meowdding.patches)

                val mods = project.layout.buildDirectory.get().toPath().resolve("tmp/extracted${sourceSet.name}RuntimeMods")
                val modsTmp = project.layout.buildDirectory.get().toPath().resolve("tmp/extracted${sourceSet.name}RuntimeMods/tmp")

                mods.deleteRecursively()
                modsTmp.createDirectories()
                mods.createDirectories()

                fun extractMods(file: java.nio.file.Path) {
                    println("Adding runtime mod ${file.name}")
                    val extracted = mods.resolve(file.name)
                    file.copyTo(extracted, overwrite = true)
                    if (!file.fileName.endsWith(".disabled.jar")) {
                        modRuntimeOnly(files(extracted))
                    }
                    ZipFile(extracted.toFile()).use {
                        it.entries().asIterator().forEach { file ->
                            val name = file.name.replace(File.separator, "/")
                            if (name.startsWith("META-INF/jars/") && name.endsWith(".jar")) {
                                val data = it.getInputStream(file).readAllBytes()
                                val file = modsTmp.resolve(name.substringAfterLast("/"))
                                file.writeBytes(data, StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.CREATE)
                                extractMods(file)
                            }
                        }
                    }
                }

                project.layout.projectDirectory.toPath().resolve("run/${sourceSet.name}Mods").takeIf { it.exists() }
                    ?.listDirectoryEntries()?.filter { it.isRegularFile() }?.forEach { file ->
                        extractMods(file)
                    }

                modsTmp.deleteRecursively()
            }

            runs {
                client {
                    jvmArgs("-Dmeowdding.overlay.test=true")
                }
            }
        }
    }

    createVersion("1.21.5", fabricApiVersion = provider { "0.127.1" }) {
        this["resourcefullib"] = libs.resourceful.lib1215
        this["resourcefulconfig"] = libs.resourceful.config1215
        this["olympus"] = libs.olympus.lib1215
        this["iris"] = libs.iris1215
    }
    createVersion("1.21.8", minecraftVersionRange = {
        start = "1.21.6"
        end = "1.21.8"
        endExclusive = false
    }) {
        this["resourcefullib"] = libs.resourceful.lib1218
        this["resourcefulconfig"] = libs.resourceful.config1218
        this["olympus"] = libs.olympus.lib1218
        this["iris"] = libs.iris1218
    }
    createVersion("1.21.9", endAtSameVersion = false, fabricApiVersion = provider { "0.133.7" }) {
        this["resourcefullib"] = libs.resourceful.lib1219
        this["resourcefulconfig"] = libs.resourceful.config1219
        this["olympus"] = libs.olympus.lib1219
        this["iris"] = libs.iris1219
    }

    mappings {
        official()
    }
}

tasks.withType<JavaCompile>().configureEach {
    options.encoding = "UTF-8"
    options.release.set(21)
}

tasks.withType<KotlinCompile>().configureEach {
    compilerOptions.jvmTarget.set(JvmTarget.JVM_21)
}

tasks.withType<KspTask> {
    outputs.upToDateWhen { false }
}

tasks.withType<Jar> {
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
}

java {
    withSourcesJar()
}

publishing {
    publications {
        create<MavenPublication>("maven") {
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

tasks.named("createCommonApiStub", GenerateStubApi::class) {
    excludes.add(libs.skyblockapi.get().module.toString())
    excludes.add("com.notkamui")
}

ksp {
    this@ksp.excludedSources.from(sourceSets.getByName("versions1215").kotlin.srcDirs)
    this@ksp.excludedSources.from(sourceSets.getByName("versions1218").kotlin.srcDirs)
    this@ksp.excludedSources.from(sourceSets.getByName("versions1219").kotlin.srcDirs)
    arg("actualStubDir", project.layout.buildDirectory.dir("generated/ksp/main/stubs").get().asFile.absolutePath)
}

meowdding {
    setupClocheClasspathFix()
    projectName = "MeowddingLib"
    generatedPackage = "me.owdding.lib.generated"
    hasAccessWideners = true
}
