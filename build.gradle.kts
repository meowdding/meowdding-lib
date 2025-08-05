@file:Suppress("UnstableApiUsage")

import com.google.devtools.ksp.gradle.KspTask
import earth.terrarium.cloche.api.metadata.ModMetadata
import earth.terrarium.cloche.tasks.GenerateFabricModJson
import net.msrandom.minecraftcodev.core.utils.toPath
import net.msrandom.minecraftcodev.fabric.task.JarInJar
import net.msrandom.minecraftcodev.runs.task.WriteClasspathFile
import net.msrandom.stubs.GenerateStubApi
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.dsl.KotlinVersion
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import kotlin.io.path.readText
import kotlin.io.path.writeText

plugins {
    java
    kotlin("jvm") version "2.1.0"
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
    maven(url = "https://maven.teamresourceful.com/repository/maven-private/")
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
val kspAll: Configuration by configurations.creating {
    isCanBeResolved = true
    isCanBeConsumed = true
}

dependencies {
    kspAll(libs.meowdding.ktmodules)
    kspAll(libs.meowdding.ktcodecs)
    kspAll("net.msrandom:kmp-actual-stubs-processor:1.0.3+workaround") {
        version { strictly("1.0.312312+workaround") } // fixes an issue with ksp stubs https://github.com/terrarium-earth/jvm-multiplatform/pull/11
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

            modImplementation(libs.hypixelapi) // included in skyblockapi
            modImplementation(libs.skyblockapi)
            modImplementation(libs.placeholders) { isTransitive = false }
            modImplementation(libs.meowdding.patches) { isTransitive = false }

            modCompileOnly(libs.rei)

            modImplementation(libs.fabric.language.kotlin)
        }
    }

    fun createVersion(
        name: String,
        version: String = name,
        loaderVersion: Provider<String> = libs.versions.fabric.loader,
        fabricApiVersion: Provider<String> = libs.versions.fabric.api,
        minecraftVersionRange: ModMetadata.VersionRange.() -> Unit = {
            start = version
            end = version
            endExclusive = false
        },
        dependencies: MutableMap<String, Provider<MinimalExternalModuleDependency>>.() -> Unit = { },
    ) {
        val dependencies = mutableMapOf<String, Provider<MinimalExternalModuleDependency>>().apply(dependencies)
        val rlib = dependencies["resourcefullib"]!!
        val rconfig = dependencies["resourcefulconfig"]!!
        val olympus = dependencies["olympus"]!!

        fabric(name) {
            includedClient()
            minecraftVersion = version
            this.loaderVersion = loaderVersion.get()

            accessWideners.from(project.layout.projectDirectory.file("src/$name/${sourceSet.name}.accesswidener"))

            //include(libs.skyblockapi)
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

                mixins.from("src/mixins/${sourceSet.name}.mixins.json")

                fun dependency(modId: String, version: Provider<String>? = null) {
                    dependency {
                        this.modId = modId
                        this.required = true
                        if (version != null) version {
                            this.start = version
                        }
                    }
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
                dependency("skyblock-api", libs.versions.skyblockapi)
                dependency("olympus", olympus.map { it.version!! })
                dependency("meowdding-patches", libs.versions.meowdding.patches)
                dependency("placeholder-api", libs.versions.placeholders)
            }

            dependencies {
                fabricApi(fabricApiVersion, minecraftVersion)
                modImplementation(olympus)
                modImplementation(rconfig)
            }

            runs {
                client()
            }
        }
    }

    createVersion("1.21.5", fabricApiVersion = provider { "0.127.1" }) {
        this["resourcefullib"] = libs.resourceful.lib1215
        this["resourcefulconfig"] = libs.resourceful.config1215
        this["olympus"] = libs.olympus.lib1215
    }
    createVersion("1.21.8", minecraftVersionRange = {
        start = "1.21.6"
    }) {
        this["resourcefullib"] = libs.resourceful.lib1218
        this["resourcefulconfig"] = libs.resourceful.config1218
        this["olympus"] = libs.olympus.lib1218
    }

    mappings { official() }
}

tasks.withType<ProcessResources>().configureEach {
    filesMatching(listOf("**/*.fsh", "**/*.vsh")) {
        filter { if (it.startsWith("//!moj_import")) "#${it.substring(3)}" else it }
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

artifacts {
    add("1215RuntimeElements", tasks["1215JarInJar"])
    add("1218RuntimeElements", tasks["1218JarInJar"])
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
}

ksp {
    this@ksp.excludedSources.from(sourceSets.getByName("1215").kotlin.srcDirs)
    this@ksp.excludedSources.from(sourceSets.getByName("1218").kotlin.srcDirs)
    arg("meowdding.project_name", "MeowddingLib")
    arg("meowdding.package", "me.owdding.lib.generated")
}

// TODO temporary workaround for a cloche issue on certain systems, remove once fixed
tasks.withType<WriteClasspathFile>().configureEach {
    actions.clear()
    actions.add {
        generate()
        val file = output.get().toPath()
        file.writeText(file.readText().lines().joinToString(File.pathSeparator))
    }
}

tasks.register("release") {
    group = "meowdding"
    sourceSets.filterNot { it.name == SourceSet.MAIN_SOURCE_SET_NAME || it.name == SourceSet.TEST_SOURCE_SET_NAME }.forEach {
            tasks.getByName("${it.name}JarInJar").let { task ->
                dependsOn(task)
                mustRunAfter(task)
            }
        }
}

tasks.register("cleanRelease") {
    group = "meowdding"
    listOf("clean", "release").forEach {
        tasks.getByName(it).let { task ->
            dependsOn(task)
            mustRunAfter(task)
        }
    }
}

tasks.withType<JarInJar>().configureEach {
    include { !it.name.endsWith("-dev.jar") }
}

tasks.withType<GenerateFabricModJson> {
    accessWidener = commonMetadata.flatMap { it.modId.map { modId -> "$modId.accessWidener" } }
}
