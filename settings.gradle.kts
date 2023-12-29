pluginManagement {
    repositories {
        maven { url = uri("https://maven.fabricmc.net/") }
        maven { url = uri("https://maven.architectury.dev/") }
        maven { url = uri("https://maven.minecraftforge.net/") }
        maven { url = uri("https://maven.neoforged.net/releases/") }
        maven { url = uri("https://maven.parchmentmc.org") }
        maven { url = uri("https://repo.spongepowered.org/repository/maven-public/") }
        maven { url = uri("https://maven.kotori316.com") }
        gradlePluginPortal()
    }
}

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version ("0.7.0")
}

includeBuild("build-logic")
include(":test-utility:common")
if (!System.getenv("DISABLE_FORGE").toBoolean()) {
    include(":test-utility:forge")
}
if (!System.getenv("DISABLE_FABRIC").toBoolean()) {
    include(":test-utility:fabric")
}
if (!System.getenv("DISABLE_NEOFORGE").toBoolean()) {
    include(":test-utility:neoforge")
}
