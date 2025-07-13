rootProject.name = "Meowdding-Lib"

pluginManagement {
    repositories {
        maven(url = "https://maven.msrandom.net/repository/cloche")
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    versionCatalogs {
        create("libs")
    }
}
