@file:Suppress("UnstableApiUsage")

@file:OptIn(ExperimentalAbiValidation::class)

import com.google.devtools.ksp.gradle.KspExtension
import me.owdding.AutoMixinExtension
import org.gradle.plugins.ide.idea.model.IdeaModel
import org.jetbrains.kotlin.gradle.dsl.KotlinProjectExtension
import org.jetbrains.kotlin.gradle.dsl.abi.ExperimentalAbiValidation
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import kotlin.io.path.Path
import kotlin.io.path.absolutePathString
import kotlin.io.path.createParentDirectories
import kotlin.io.path.readBytes
import kotlin.io.path.writeBytes

plugins {
    kotlin("jvm")
    id("me.owdding.auto-mixins")
    id("com.google.devtools.ksp")
    id("versioned-catalogues")
    id("idea")
}

kotlin {
    compilerOptions {
        freeCompilerArgs.add("-Xrender-internal-diagnostic-names")
    }
}

base {
    archivesName.set("$archiveName-${archivesName.get()}")
}

private val stonecutter = project.extensions.getByName("stonecutter") as dev.kikugie.stonecutter.build.StonecutterBuildExtension
fun isUnobfuscated() = stonecutter.eval(stonecutter.current.version, ">=26.1")

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
fun makeAlias(configuration: String) = if (isUnobfuscated()) configuration else "mod" + configuration.replaceFirstChar { it.uppercase() }

val maybeModImplementation = makeAlias("implementation")
val maybeModCompileOnly = makeAlias("compileOnly")
val maybeModRuntimeOnly = makeAlias("runtimeOnly")
val maybeModApi = makeAlias("api")

val archiveName = "Meowdding-Lib"


tasks.named("build") {

    val files = tasks.named("remapJar").map { it.outputs.files }
    inputs.properties(
        "project_name" to project.name,
        "project_dir" to rootProject.projectDir.toPath().absolutePathString(),
        "mc_version" to stonecutter.current.version,
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
}

tasks.withType<KotlinCompile>().configureEach {
    compilerOptions.optIn.add("kotlin.time.ExperimentalTime")
    compilerOptions.freeCompilerArgs.addAll(
        "-Xcontext-parameters",
        "-Xcontext-sensitive-resolution",
        "-Xnullability-annotations=@org.jspecify.annotations:warn"
    )
}

val accessWidenerFile = rootProject.file("src/sbapi.accesswidener")

tasks.withType<ProcessResources>().configureEach {
    filteringCharset = "UTF-8"
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

tasks.named<ProcessResources>("processResources") {
    val range = if (versionedCatalog.versions.has("minecraft.range")) {
        versionedCatalog.versions.get("minecraft.range").toString()
    } else {
        val start = versionedCatalog.versions.getOrFallback("minecraft.start", "minecraft")
        val end = versionedCatalog.versions.getOrFallback("minecraft.end", "minecraft")
        ">=$start <=$end"
    }

    val replacements = mapOf(
        "version" to project.version,
        "minecraft_range" to range,
        "version" to version,
        "fabric_lang_kotlin" to versionedCatalog.versions["fabric.language.kotlin"],
        "sbapi" to versionedCatalog.versions["skyblockapi"],
        "rlib" to versionedCatalog.versions["resourceful.lib"],
        "olympus" to versionedCatalog.versions["olympus"],
        "placeholder_api" to versionedCatalog.versions["placeholders"]
    )
    inputs.properties(replacements)

    filesMatching("fabric.mod.json") {
        expand(replacements)
    }
}

(extensions.getByName("base") as BasePluginExtension).apply {
    archivesName = archiveName
}

tasks.withType<Jar> {
    duplicatesStrategy = DuplicatesStrategy.INCLUDE
}

val javaVersion get() = if (isUnobfuscated()) 25 else 21

tasks.withType<JavaCompile>().configureEach {
    options.encoding = "UTF-8"
    options.release.set(javaVersion)
}


extensions.getByName<JavaPluginExtension>("java").apply {
    toolchain.languageVersion = JavaLanguageVersion.of(javaVersion)
    withSourcesJar()
}

afterEvaluate {
    extensions.getByName<KotlinProjectExtension>("kotlin").apply {
        jvmToolchain(javaVersion)
    }

    if (!isUnobfuscated()) {
        tasks.named<AbstractArchiveTask>("remapJar") {
            archiveClassifier = stonecutter.current.version
        }
    }
}

extensions.getByType<KspExtension>().apply {
    arg("meowdding.project_name", "MeowddingLib")
    arg("meowdding.package", "me.owdding.lib.generated")
}

extensions.getByType<AutoMixinExtension>().apply {
    mixinPackage = "me.owdding.lib.mixins"
    projectName = "meowdding-lib"
}

extensions.getByType<IdeaModel>().apply {
    module {
        isDownloadJavadoc = true
        isDownloadSources = true

        excludeDirs.add(file("run"))
    }
}

kotlin {
    jvmToolchain(if (isUnobfuscated()) 25 else 21)
}

java {
    targetCompatibility = if (isUnobfuscated()) JavaVersion.VERSION_21 else JavaVersion.VERSION_25
    sourceCompatibility = JavaVersion.VERSION_21
}

dependencies {
    "minecraft"(versionedCatalog["minecraft"])

    maybeModImplementation(versionedCatalog["fabric.api"])

    includeImplementation(versionedCatalog["resourceful.config"])
    includeImplementation(versionedCatalog["resourceful.lib"])
    includeImplementation(versionedCatalog["placeholders"])
    includeImplementation(versionedCatalog["olympus"])
    includeImplementation(versionedCatalog["meowdding.remote.repo"])
    maybeModImplementation(versionedCatalog["resourceful.config.kotlin"])


    "api"(versionedCatalog["skyblockapi"]) {
        capabilities {
            requireCapability("tech.thatgravyboat:skyblock-api-${stonecutter.current.version}")
        }
    }

    includeImplementation(versionedCatalog["keval"], remap = false)

    maybeModImplementation(versionedCatalog["fabric.language.kotlin"])
    "compileOnly"(versionedCatalog["fabric.loader"])


    "compileOnly"(versionedCatalog["meowdding.ktmodules"])
    "compileOnly"(versionedCatalog["meowdding.ktcodecs"])

    "ksp"(versionedCatalog["meowdding.ktmodules"])
    "ksp"(versionedCatalog["meowdding.ktcodecs"])

    maybeModImplementation(versionedCatalog["hypixelapi"])

    "include"(versionedCatalog["meowdding.patches"])
    includeImplementation(versionedCatalog["moulberry.mixinconstraints"])

    "compileOnly"(versionedCatalog["iris"])
    maybeModRuntimeOnly(versionedCatalog["rei"])
}

fun DependencyHandlerScope.includeImplementation(dep: Any, transitive: Boolean = true, remap: Boolean = true) {
    "include"(dep)
    when {
        transitive && !remap -> maybeModApi(dep)
        remap -> maybeModImplementation(dep)
        else -> maybeModImplementation(dep)
    }
}

tasks.named<Jar>("jar") {
    archiveBaseName = archiveName
    archiveClassifier = "${stonecutter.current.version}-dev"
}

tasks.named<Jar>("sourcesJar") {
    archiveBaseName = archiveName
    archiveClassifier = "${stonecutter.current.version}-sources"
    duplicatesStrategy = DuplicatesStrategy.INCLUDE
}

idea {
    module {
        isDownloadJavadoc = true
        isDownloadSources = true

        excludeDirs.add(file("run"))
    }
}
