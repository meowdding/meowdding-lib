@file:Suppress("UnstableApiUsage")

import net.fabricmc.loom.task.RemapJarTask
import net.fabricmc.loom.task.ValidateAccessWidenerTask
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import kotlin.io.path.Path
import kotlin.io.path.absolutePathString
import kotlin.io.path.createParentDirectories
import kotlin.io.path.readBytes
import kotlin.io.path.writeBytes

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
    fun scopedMaven(url: String, vararg paths: String) = maven(url) { content { paths.forEach(::includeGroupAndSubgroups) } }

    scopedMaven("https://pkgs.dev.azure.com/djtheredstoner/DevAuth/_packaging/public/maven/v1", "me.djtheredstoner")
    scopedMaven("https://repo.hypixel.net/repository/Hypixel", "net.hypixel")
    scopedMaven("https://maven.parchmentmc.org/", "org.parchmentmc")
    scopedMaven("https://api.modrinth.com/maven", "maven.modrinth")
    scopedMaven(
        "https://maven.teamresourceful.com/repository/maven-public/",
        "earth.terrarium",
        "com.teamresourceful",
        "tech.thatgravyboat",
        "me.owdding",
        "com.terraformersmc"
    )
    scopedMaven("https://maven.nucleoid.xyz/", "eu.pb4")
    scopedMaven(url = "https://maven.shedaniel.me/", "me.shedaniel", "dev.architectury")
    mavenCentral()
}

dependencies {

    minecraft(versionedCatalog["minecraft"])
    mappings(loom.layered {
        officialMojangMappings()
        parchment(variantOf(versionedCatalog["parchment"]) {
            artifactType("zip")
        })
    })

    modImplementation(versionedCatalog["fabric.api"])

    includeImplementation(versionedCatalog["resourceful.config"])
    includeImplementation(versionedCatalog["resourceful.lib"])
    includeImplementation(versionedCatalog["placeholders"])
    includeImplementation(versionedCatalog["olympus"])
    includeImplementation(libs.meowdding.remote.repo)
    modImplementation(libs.resourceful.config.kotlin)

    api(libs.skyblockapi) {
        capabilities { requireCapability("tech.thatgravyboat:skyblock-api-${stonecutter.current.version}") }
    }

    includeImplementation(libs.keval, false)

    modImplementation(libs.fabric.language.kotlin)
    compileOnly(libs.fabric.loader)

    compileOnly(libs.meowdding.ktmodules)
    compileOnly(libs.meowdding.ktcodecs)

    ksp(libs.meowdding.ktmodules)
    ksp(libs.meowdding.ktcodecs)

    modImplementation(libs.hypixelapi)

    include(libs.meowdding.patches)
    includeImplementation(libs.meowdding.remote.repo)
    includeImplementation(libs.moulberry.mixinconstraints)

    compileOnly(versionedCatalog["iris"])
    modCompileOnly(libs.rei)
}

fun DependencyHandler.includeImplementation(dep: Any, remap: Boolean = true) {
    include(dep)
    if (remap) modImplementation(dep) else implementation(dep)
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

    val files = tasks.named("remapJar").map { it.outputs.files }
    inputs.properties(
        "project_name" to project.name,
        "project_dir" to rootProject.projectDir.toPath().absolutePathString(),
        "mc_version" to project.stonecutter.current.version,
        "version" to project.version.toString(),
        "archive_name" to archiveName
    )

    doLast {
        val from = files.get().files.first().toPath()
        val projectDir = this.inputs.properties["project_dir"].toString()
        val version = this.inputs.properties["version"]
        val mcVersion = this.inputs.properties["mc_version"]
        val archiveName = this.inputs.properties["archive_name"]

        val targetFile = Path(projectDir).resolve("build/libs/${archiveName}-$version-${mcVersion}.jar")
        targetFile.createParentDirectories()
        targetFile.writeBytes(from.readBytes())
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
        "-Xcontext-sensitive-resolution",
        "-Xnullability-annotations=@org.jspecify.annotations:warn"
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

tasks.named<Jar>("jar") {
    archiveBaseName = archiveName
    archiveClassifier = "${stonecutter.current.version}-dev"
}

tasks.named<Jar>("sourcesJar") {
    archiveBaseName = archiveName
    archiveClassifier = "${stonecutter.current.version}-sources"
    duplicatesStrategy = DuplicatesStrategy.INCLUDE
}

tasks.named<RemapJarTask>("remapJar") {
    archiveBaseName = archiveName
    archiveClassifier = stonecutter.current.version
}
