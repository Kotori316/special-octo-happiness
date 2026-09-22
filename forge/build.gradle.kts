import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

plugins {
    id("com.kotori316.common.java")
    id("com.kotori316.common.publish")
    // id("com.kotori316.common.signing")
    alias(libs.plugins.forge.gradle)
    // id("org.spongepowered.mixin") version ("0.7.38")
}

val modId = "debug_util"

minecraft {
    runs {
        register("client") {
            workingDir.convention(layout.projectDirectory.dir("run"))
            systemProperty("forge.logging.markers", "REGISTRIES")
            systemProperty("mixin.env.remapRefMap", "true")
            systemProperty("mixin.env.refMapRemappingFile", "${projectDir}/build/createSrgToMcp/output.srg")
            systemProperty("mixin.debug.export", "true")
            systemProperty("forge.logging.console.level", "debug")
            systemProperty("eventbus.api.strictRuntimeChecks", "true")
            systemProperty("terminal.ansi", "true")
            if (System.getProperty("os.name").startsWith("Mac")) {
                jvmArgs("-XstartOnFirstThread")
            }
            args("--username", "Kotori")
            mods {
                create(modId) {
                    source(sourceSets["main"])
                }
            }
        }
    }
}

repositories {
    minecraft.mavenizer(this)
    maven(fg.forgeMaven)
    maven(fg.minecraftLibsMaven)
}

val commonProject = project.project(":common")

dependencies {
    implementation(minecraft.dependency("net.minecraftforge:forge:${project.property("forge_version")}"))
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

sourceSets.forEach {
    val dir = layout.buildDirectory.dir("sourcesSets/forge-${it.name}")
    it.output.setResourcesDir(dir)
    it.java.destinationDirectory = dir
}
