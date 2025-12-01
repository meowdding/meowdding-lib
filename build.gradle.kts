@file:Suppress("UnstableApiUsage")

import kotlin.jvm.java
import net.fabricmc.loom.task.ValidateAccessWidenerTask
import org.gradle.kotlin.dsl.version
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    idea
    java
    kotlin("jvm") version "2.2.0"
    id("fabric-loom")
    alias(libs.plugins.kotlin.symbol.processor)
    alias(libs.plugins.meowdding.auto.mixins)
    `versioned-catalogues`
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
    mavenCentral()
    mavenLocal()
}

configurations {
    modImplementation {
        attributes.attribute(Attribute.of("earth.terrarium.cloche.modLoader", String::class.java), "fabric")
    }
}

dependencies {
    attributesSchema {
        attribute(Attribute.of("earth.terrarium.cloche.minecraftVersion", String::class.java)) {
            disambiguationRules.add(ClocheDisambiguationRule::class) {
                params(versionedCatalog.versions.getOrFallback("sbapi-mc-version", "minecraft").toString())
            }
        }
    }

    minecraft(versionedCatalog["minecraft"])
    mappings(loom.layered {
        officialMojangMappings()
        parchment(variantOf(versionedCatalog["parchment"]) {
            artifactType("zip")
        })
    })

    includeImplementation(versionedCatalog["resourceful.config"])
    includeImplementation(versionedCatalog["resourceful.lib"])
    includeImplementation(versionedCatalog["placeholders"])
    implementation(libs.resourceful.config.kotlin)
    includeImplementation(versionedCatalog["olympus"])
    includeImplementation(libs.meowdding.remote.repo)
    implementation(libs.skyblockapi)

    includeImplementation(libs.keval)

    modImplementation(versionedCatalog["fabric.api"])
    modImplementation(libs.fabric.language.kotlin)
    compileOnly(libs.fabric.loader)

    compileOnly(libs.meowdding.ktmodules)
    compileOnly(libs.meowdding.ktcodecs)

    ksp(libs.meowdding.ktmodules)
    ksp(libs.meowdding.ktcodecs)

    implementation(libs.hypixelapi)

    include(libs.meowdding.patches)
    includeImplementation(libs.meowdding.remote.repo)
    includeImplementation(libs.moulberry.mixinconstraints)

    compileOnly(versionedCatalog["iris"])
    modCompileOnly(libs.rei)
}

fun DependencyHandler.includeImplementation(dep: Any) {
    include(dep)
    modImplementation(dep)
}

val mcVersion = stonecutter.current.version.replace(".", "")
val accessWidenerFile: File = rootProject.file("src/mlib.accesswidener")
loom {
    runConfigs["client"].apply {
        ideConfigGenerated(true)
        runDir = "../../run"
        vmArg("-Dfabric.modsFolder=" + '"' + "${mcVersion}Mods" + '"')
    }

    if (accessWidenerFile.exists()) {
        accessWidenerPath.set(accessWidenerFile)
    }
}

val archiveName = "Meowdding-Lib"

ksp {
    arg("meowdding.project_name", "MeowddingLib")
    arg("meowdding.package", "me.owdding.lib.generated")
}

java {
    toolchain.languageVersion = JavaLanguageVersion.of(21)
    withSourcesJar()
}

base {
    archivesName.set("$archiveName-${archivesName.get()}")
}

tasks.named("build") {
    doLast {
        val sourceFile = rootProject.projectDir.resolve("versions/${project.name}/build/libs/${archiveName}-${stonecutter.current.version}-$version.jar")
        val targetFile = rootProject.projectDir.resolve("build/libs/${archiveName}-$version-${stonecutter.current.version}.jar")
        targetFile.parentFile.mkdirs()
        targetFile.writeBytes(sourceFile.readBytes())
    }
}

tasks.withType<JavaCompile>().configureEach {
    options.encoding = "UTF-8"
    options.release.set(21)
}

tasks.withType<KotlinCompile>().configureEach {
    compilerOptions.jvmTarget.set(JvmTarget.JVM_21)
    compilerOptions.optIn.add("kotlin.time.ExperimentalTime")
    compilerOptions.freeCompilerArgs.addAll(
        "-Xcontext-parameters",
        "-Xcontext-sensitive-resolution"
    )
}

tasks.processResources {
    val replacements = mapOf(
        "version" to version,
        "minecraft_start" to versionedCatalog.versions.getOrFallback("minecraft.start", "minecraft"),
        "minecraft_end" to versionedCatalog.versions.getOrFallback("minecraft.end", "minecraft"),
        "fabric_lang_kotlin" to libs.versions.fabric.language.kotlin.get(),
        "sbapi" to libs.versions.skyblockapi.get(),
        "rlib" to versionedCatalog.versions["resourceful.lib"],
        "olympus" to versionedCatalog.versions["olympus"],
        "placeholder_api" to versionedCatalog.versions["placeholders"]
    )
    inputs.properties(replacements)

    filesMatching("fabric.mod.json") {
        expand(replacements)
    }
}

autoMixins {
    mixinPackage = "me.owdding.lib.mixins"
    projectName = "meowdding-lib"
}

tasks.withType<ProcessResources>().configureEach {
    duplicatesStrategy = DuplicatesStrategy.INCLUDE
    filesMatching(listOf("**/*.fsh", "**/*.vsh")) {
        // `#` is used for all versions, `!` is used for multiversioned imports
        filter { if (it.startsWith("//#moj_import") || it.startsWith("//!moj_import")) "#${it.substring(3)}" else it }
    }
    with(copySpec {
        from(rootProject.file("src/lang")).include("*.json").into("assets/meowdding-lib/lang")
    })
    with(copySpec {
        from(accessWidenerFile)
    })
}

idea {
    module {
        isDownloadJavadoc = true
        isDownloadSources = true

        excludeDirs.add(file("run"))
    }
}

tasks.withType<ValidateAccessWidenerTask> { enabled = false }

tasks.named<Jar>("sourcesJar") {
    duplicatesStrategy = DuplicatesStrategy.INCLUDE
}
