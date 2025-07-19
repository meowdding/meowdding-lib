rootProject.name = "meowdding-lib"

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
