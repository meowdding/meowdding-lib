import dev.kikugie.stonecutter.build.config.ReplacementContainer
import org.gradle.kotlin.dsl.support.serviceOf
import org.jetbrains.kotlin.gradle.plugin.KotlinPlatformType

plugins {
    id("dev.kikugie.stonecutter")
    id("net.fabricmc.fabric-loom-remap") version "1.17-SNAPSHOT" apply false
    id("net.fabricmc.fabric-loom") version "1.17-SNAPSHOT" apply false
    kotlin("jvm") apply false
    `maven-publish`
}

stonecutter active "26.3"

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

    fun ReplacementContainer.StringReplacementSpec.rename(
        from: String,
        to: String,
        prefix: String? = null
    ) {
        val prefix = prefix?.plus(".") ?: ""
        replace("import $prefix$from;", "import $prefix$to;")
        replace("import $prefix$from as ${to.substringAfterLast('.')}", "import $prefix$to")
    }
    fun ReplacementContainer.StringReplacementSpec.move(
        from: String,
        to: String,
        prefix: String? = null
    ) {
        val prefix = prefix?.plus(".") ?: ""
        replace("$prefix$from".replace('/', '.'), "$prefix$to".replace('/', '.'))
        replace("$prefix$from".replace('.', '/'), "$prefix$to".replace('.', '/'))
    }

    class PackageMover(val fromPackage: String, val toPackage: String) {
        fun ReplacementContainer.StringReplacementSpec.moveRelative(vararg names: String) {
            names.forEach { name ->
                move("$fromPackage.$name", "$toPackage.$name")
            }
        }
        fun ReplacementContainer.StringReplacementSpec.move(vararg relatives: Pair<String, String>) {
            relatives.forEach { (old, new) ->
                move("$fromPackage.$old", "$toPackage.$new")
            }
        }
        fun ReplacementContainer.StringReplacementSpec.moveAndRename(vararg relatives: Pair<String, String>) {
            relatives.forEach { (old, new) ->
                rename("$fromPackage.$old", "$toPackage.$new")
            }
        }
    }

    fun movePackage(
        from: String,
        to: String,
        callback: PackageMover.() -> Unit
    ) = PackageMover(from, to).callback()

    val minecraft = "net.minecraft"

    replacements {
        string(current.parsed > "26.2") {
            val blaze3d = "com.mojang.blaze3d"
            val renderpearl = "com.mojang.renderpearl.api"

            rename("EnderMan", "Enderman", "$minecraft.world.entity.monster")
            rename("DynamicUniformStorage", "DynamicGpuDataStorage", "$minecraft.client.renderer")
            move("me.owdding.lib.platform.screens.*", "net.minecraft.client.input.*")

            move("me.owdding.lib.platform.screens.BaseParentWidget", "earth.terrarium.olympus.client.components.base.BaseParentWidget")

            movePackage(blaze3d, renderpearl) {
                moveRelative(
                    "GpuFormat",
                    "buffers.GpuBuffer",
                    "buffers.GpuBufferSlice",
                    "pipeline.BindGroupLayout",
                    "pipeline.BlendFunction",
                    "pipeline.ColorTargetState",
                    "pipeline.DepthStencilState",
                    "pipeline.RenderPipeline",
                    "textures.FilterMode",
                    "textures.GpuTexture",
                    "textures.GpuTextureView",
                    "vertex.VertexFormat",
                )
                move(
                    "systems.RenderPass" to "commands.RenderPass",
                    "systems.GpuDevice" to "device.GpuDevice",
                    "platform.CompareOp" to "pipeline.CompareOp",
                    "IndexType" to "pipeline.IndexType",
                    "PrimitiveTopology" to "pipeline.PrimitiveTopology",
                    "shaders.UniformType" to "pipeline.UniformType",
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

stonecutter.versions.forEach { (project, version) ->
    val gradleFriendlyVersion = version.replace(".", "")
    val project = project(project)


    val apiElements = configurations.create(gradleFriendlyVersion + "apiElements") {
        isCanBeResolved = false
        isCanBeConsumed = true

        attributes {
            attribute(Usage.USAGE_ATTRIBUTE, objects.named(Usage.JAVA_API))
            attribute(Bundling.BUNDLING_ATTRIBUTE, objects.named(Bundling.EXTERNAL))
            attribute(TargetJvmVersion.TARGET_JVM_VERSION_ATTRIBUTE, 25)
            attribute(TargetJvmEnvironment.TARGET_JVM_ENVIRONMENT_ATTRIBUTE, objects.named(TargetJvmEnvironment.STANDARD_JVM))
            attribute(KotlinPlatformType.attribute, KotlinPlatformType.jvm)
            attribute(Category.CATEGORY_ATTRIBUTE, objects.named(Category.LIBRARY))
            attribute(LibraryElements.LIBRARY_ELEMENTS_ATTRIBUTE, objects.named(LibraryElements.JAR))
            attribute(minecraftVersionAttribute, version)
            attribute(remappedAttribute, "false")
        }

        project.afterEvaluate {
            this@create.dependencies.addAll(configurations.named("api").get().dependencies)
            outgoing.artifact(tasks.named("jar"))
        }

        outgoing.capability("me.owdding.meowdding-lib:meowdding-lib-$version:${rootProject.version}")
        outgoing.capability("me.owdding.meowdding-lib:meowdding-lib:${rootProject.version}")
    }
    val runtimeElements = configurations.create(gradleFriendlyVersion + "runtimeElements") {
        isCanBeResolved = false
        isCanBeConsumed = true

        attributes {
            attribute(Usage.USAGE_ATTRIBUTE, objects.named(Usage.JAVA_RUNTIME))
            attribute(Bundling.BUNDLING_ATTRIBUTE, objects.named(Bundling.EXTERNAL))
            attribute(TargetJvmVersion.TARGET_JVM_VERSION_ATTRIBUTE, 25)
            attribute(TargetJvmEnvironment.TARGET_JVM_ENVIRONMENT_ATTRIBUTE, objects.named(TargetJvmEnvironment.STANDARD_JVM))
            attribute(KotlinPlatformType.attribute, KotlinPlatformType.jvm)
            attribute(Category.CATEGORY_ATTRIBUTE, objects.named(Category.LIBRARY))
            attribute(LibraryElements.LIBRARY_ELEMENTS_ATTRIBUTE, objects.named(LibraryElements.JAR))
            attribute(minecraftVersionAttribute, version)
            attribute(remappedAttribute, "false")
        }

        project.afterEvaluate {
            this@create.dependencies.addAll(configurations.named("runtimeOnly").get().dependencies)
            this@create.dependencies.addAll(configurations.named("api").get().dependencies)
            outgoing.artifact(tasks.named("jar"))
        }

        outgoing.capability("me.owdding.meowdding-lib:meowdding-lib-$version:${rootProject.version}")
        outgoing.capability("me.owdding.meowdding-lib:meowdding-lib:${rootProject.version}")
    }

    val sourcesElements = configurations.create(gradleFriendlyVersion + "sources") {
        isCanBeResolved = false
        isCanBeConsumed = true

        attributes {
            attribute(Usage.USAGE_ATTRIBUTE, objects.named(Usage.JAVA_RUNTIME))
            attribute(Category.CATEGORY_ATTRIBUTE, objects.named(Category.DOCUMENTATION))
            attribute(Bundling.BUNDLING_ATTRIBUTE, objects.named(Bundling.EXTERNAL))
            attribute(DocsType.DOCS_TYPE_ATTRIBUTE, objects.named(DocsType.SOURCES))
            attribute(minecraftVersionAttribute, version)
            attribute(remappedAttribute, "false")
        }

        project.afterEvaluate {
            outgoing.artifact(tasks.named("sourcesJar"))
        }

        outgoing.capability("me.owdding.meowdding-lib:meowdding-lib-$version:${rootProject.version}")
        outgoing.capability("me.owdding.meowdding-lib:meowdding-lib:${rootProject.version}")
    }

    sbapiComponent.addVariantsFromConfiguration(apiElements) {
        mapToOptional()
    }
    sbapiComponent.addVariantsFromConfiguration(runtimeElements) {
        mapToOptional()
    }
    sbapiComponent.addVariantsFromConfiguration(sourcesElements) {
        mapToOptional()
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
