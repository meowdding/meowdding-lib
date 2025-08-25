rootProject.name = "meowdding-lib"

pluginManagement {
    repositories {
        maven(url = "https://maven.msrandom.net/repository/cloche")
        maven(url = "https://maven.teamresourceful.com/repository/maven-public/")
        maven(url = "https://maven.teamresourceful.com/repository/maven-private/")
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    versionCatalogs {
        create("libs")
    }
}
