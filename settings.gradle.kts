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
    id("org.gradle.toolchains.foojay-resolver-convention") version ("0.8.0")
    id("com.gradle.develocity") version ("3.+")
}

develocity {
    buildScan {
        if (System.getenv("CI").toBoolean()) {
            termsOfUseUrl = "https://gradle.com/help/legal-terms-of-use"
            termsOfUseAgree = "yes"
        }
        publishing {
            onlyIf { false }
        }
    }
}

includeBuild("build-logic")
include(":test-utility:common")
include(":debug-utility:common")
if (!System.getenv("DISABLE_FORGE").toBoolean()) {
    if (!System.getenv("DISABLE_TU").toBoolean()) {
        include(":test-utility:forge")
    }
    if (!System.getenv("DISABLE_DU").toBoolean()) {
        include(":debug-utility:forge")
    }
}
if (!System.getenv("DISABLE_FABRIC").toBoolean()) {
    if (!System.getenv("DISABLE_TU").toBoolean()) {
        include(":test-utility:fabric")
    }
    if (!System.getenv("DISABLE_DU").toBoolean()) {
        include(":debug-utility:fabric")
    }
}
if (!System.getenv("DISABLE_NEOFORGE").toBoolean()) {
    if (!System.getenv("DISABLE_TU").toBoolean()) {
        include(":test-utility:neoforge")
    }
    if (!System.getenv("DISABLE_DU").toBoolean()) {
        include(":debug-utility:neoforge")
    }
}
