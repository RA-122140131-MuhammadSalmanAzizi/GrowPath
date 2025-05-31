pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev") // buat plugin compose jika perlu
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.PREFER_SETTINGS)
    repositories {
        google()
        mavenCentral()
        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev") // buat plugin compose jika perlu
        maven("https://jitpack.io") // Untuk UCrop library
    }
}
rootProject.name = "GrowPath"
include(":app")
