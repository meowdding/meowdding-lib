plugins {
    `kotlin-dsl`
    kotlin("plugin.serialization") version libs.versions.kotlin.version.get()
}

repositories {
    gradlePluginPortal()
    maven("https://maven.teamresourceful.com/repository/maven-public/")
    maven("https://maven.kikugie.dev/snapshots")
}

dependencies {
    implementation("net.peanuuutz.tomlkt:tomlkt:0.5.0")
    implementation("org.jetbrains.kotlin:kotlin-stdlib")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.8.1")
}
fun plugin(provider: Provider<PluginDependency>): Provider<String> = provider.map {
    "${it.pluginId}:${it.pluginId}.gradle.plugin:${it.version}"
}

java.sourceCompatibility = JavaVersion.VERSION_25
java.targetCompatibility = JavaVersion.VERSION_25

kotlin.jvmToolchain(25)

dependencies {
    implementation(libs.kotlin.stdlib)
    implementation(libs.kotlin.gradle.plugin)
    implementation(libs.kotlin.gradle.plugin.api)
    implementation(plugin(libs.plugins.kotlin.symbol.processor))
    implementation(plugin(libs.plugins.meowdding.auto.mixins))
    implementation("dev.kikugie.stonecutter:dev.kikugie.stonecutter.gradle.plugin:0.10-alpha.2")
}
