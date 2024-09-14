import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

plugins {
    id("com.kotori316.common.java")
    id("com.kotori316.common.publish")
    id("com.kotori316.common.signing")
    alias(libs.plugins.neoforge.gradle)
}

val modId = "debug_utility"

subsystems {
    parchment {
        minecraftVersion = project.property("parchment_minecraft") as String
        mappingsVersion = project.property("parchment_mapping") as String
    }
}

val commonProject = project.project(":common")
dependencies {
    implementation(group = "net.neoforged", name = "neoforge", version = project.property("neo_version").toString())

    compileOnly(commonProject)
}

tasks.processResources {
    from(commonProject.sourceSets.main.get().resources)
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
}

tasks.named("compileJava", JavaCompile::class).configure {
    source(commonProject.sourceSets.main.get().allSource)
}

val jarAttributeMap = mapOf(
    "Specification-Title" to modId,
    "Specification-Vendor" to "Kotori316",
    "Specification-Version" to "1", // We are version 1 of ourselves
    "Implementation-Title" to project.name,
    "Implementation-Version" to tasks.jar.get().archiveVersion,
    "Implementation-Vendor" to "Kotori316",
    "Implementation-Timestamp" to ZonedDateTime.now().format(DateTimeFormatter.ISO_INSTANT),
    "MixinConfigs" to "${modId}.mixins.json",
    "Automatic-Module-Name" to modId,
)

tasks.jar {
    manifest {
        attributes(jarAttributeMap)
    }
}
