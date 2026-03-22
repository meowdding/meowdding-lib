import net.fabricmc.loom.task.ValidateAccessWidenerTask
import org.gradle.kotlin.dsl.withType

plugins {
    id("net.fabricmc.fabric-loom")
    `mlib-setup`
}

val mcVersion = stonecutter.current.version.replace(".", "")
val accessWidenerFile = rootProject.file("src/mlib.accesswidener")

loom {
    runConfigs["client"].apply {
        ideConfigGenerated(true)
        runDir = "../../run"
        vmArg("-Dfabric.modsFolder=\"${mcVersion}Mods\"")
    }

    if (accessWidenerFile.exists()) {
        accessWidenerPath.set(accessWidenerFile)
    }
}

tasks.withType<ValidateAccessWidenerTask> { enabled = false }
