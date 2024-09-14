import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

plugins {
    id("com.kotori316.common.java")
    id("com.kotori316.common.publish")
    id("com.kotori316.common.signing")
    id("net.minecraftforge.gradle") version ("[6.0,6.2)")
    id("org.parchmentmc.librarian.forgegradle") version ("1.+")
    id("org.spongepowered.mixin") version ("0.7.38")
}

val modId = "debug_util"

minecraft {
    val mappingVersion =
        "${project.property("parchment_minecraft")}-${project.property("parchment_mapping")}-${project.property("minecraft")}"
    mappings(
        mapOf(
            "channel" to "parchment",
            "version" to mappingVersion,
        )
    )
    reobf = false
    runs {
        create("client") {
            workingDirectory(project.file("run"))
            property("forge.logging.markers", "REGISTRIES")
            property("mixin.env.remapRefMap", "true")
            property("mixin.env.refMapRemappingFile", "${projectDir}/build/createSrgToMcp/output.srg")
            property("mixin.debug.export", "true")
            property("forge.logging.console.level", "debug")
            args("--username", "Kotori")

            mods {
                create(modId) {
                    source(sourceSets["main"])
                }
            }
        }
    }
}

val commonProject = project.project(":common")

dependencies {
    minecraft("net.minecraftforge:forge:${project.property("forge_version")}")
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


sourceSets.main {
    val dir = layout.buildDirectory.dir("sourcesSets/$name")
    output.setResourcesDir(dir)
    java.destinationDirectory = dir
}

idea {
    module {
        isDownloadSources = true
        isDownloadJavadoc = true
    }
}
