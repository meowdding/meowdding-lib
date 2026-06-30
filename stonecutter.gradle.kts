import org.gradle.api.internal.component.SoftwareComponentInternal
import org.gradle.kotlin.dsl.support.serviceOf

plugins {
    id("dev.kikugie.stonecutter")
    id("net.fabricmc.fabric-loom-remap") version "1.16-SNAPSHOT" apply false
    id("net.fabricmc.fabric-loom") version "1.16-SNAPSHOT" apply false
    kotlin("jvm") apply false
    `maven-publish`
}

stonecutter active "26.2"

stonecutter handlers {
    //configure("fsh", "vsh") {
    //    commenter = line("//")
    //}
}

stonecutter parameters {
    swaps["mod_version"] = "\"" + property("version") + "\";"
    swaps["minecraft"] = "\"" + node.metadata.version + "\";"

    Replacements.read(project).replacements.forEach { (name, replacement) ->
        when (replacement) {
            is StringReplacement  -> replacements.string {
                if (replacement.named) {
                    id = name
                }
                direction = eval(current.version, replacement.condition)
                replace(replacement.from, replacement.to)
            }

            is RegexReplacement -> replacements.regex {
                if (replacement.named) {
                    id = name
                }
                direction = eval(current.version, replacement.condition)
                replace(
                    replacement.regex to replacement.to,
                    replacement.reverseRegex to replacement.reverse
                )
            }
        }
    }
}

//<editor-fold desc="Publishing setup">
val componentFactory = project.serviceOf<SoftwareComponentFactory>()
val sbapiComponent = componentFactory.adhoc("sbapi")
val minecraftVersionAttribute = Attribute.of("net.minecraft.version", String::class.java)
val remappedAttribute = Attribute.of("net.fabricmc.remapped", String::class.java)

evaluationDependsOnChildren()

stonecutter.versions.forEach { (project, version) ->
    val gradleFriendlyVersion = version.replace(".", "")
    val project = project(project)

    afterEvaluate {
        (project.components.get("java") as SoftwareComponentInternal).usages.forEach { context ->
            val configuration = configurations.create(gradleFriendlyVersion + context.name) {
                isCanBeResolved = false
                isCanBeConsumed = true

                attributes.addAllLater(context.attributes)

                dependencies.addAll(context.dependencies)
                outgoing.artifacts.addAll(context.artifacts)
                context.capabilities.forEach {
                    outgoing.capability("${it.group}:${it.name}-$version:${it.version}")
                    outgoing.capability("${it.group}:${it.name}:${it.version}")
                }
            }
            sbapiComponent.addVariantsFromConfiguration(configuration) { mapToOptional() }
        }
    }
}

publishing {
    publications {
        create("meowdding-lib", MavenPublication::class.java) {
            from(sbapiComponent)
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
//</editor-fold>
