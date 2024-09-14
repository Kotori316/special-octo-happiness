import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

buildscript {
    repositories {
        mavenCentral()
    }
}
plugins {
    id("com.kotori316.tu.common")
    id("com.kotori316.tu.platforms")
    alias(libs.plugins.neoforge.gradle)
}

val mod_id = "test_utility_neo"

println(
    "Java: ${System.getProperty("java.version")}, JVM: ${System.getProperty("java.vm.version")} (${
        System.getProperty(
            "java.vendor"
        )
    }), Arch: ${System.getProperty("os.arch")}"
)
runs {
    /*client {
        workingDirectory.set(project.file("Minecraft"))
        systemProperties.put("forge.logging.markers", "REGISTRIES")
        systemProperties.put("mixin.env.remapRefMap", "true")
        systemProperties.put("mixin.env.refMapRemappingFile", "${projectDir}/build/createSrgToMcp/output.srg")
        systemProperties.put("mixin.debug.export", "true")
        systemProperties.put("forge.logging.console.level", "debug")
        programArguments.addAll('--username', "Kotori")

        modSources.add(project.sourceSets.main)
    }*/
    create("junit") {
        unitTestSources.add("test", project.sourceSets["main"], project.sourceSets["test"])
    }
}

subsystems {
    parchment {
        minecraftVersion = project.property("parchment_minecraft") as String
        mappingsVersion = project.property("parchment_mapping") as String
    }
}

repositories {
    mavenCentral()
    maven {
        url = uri("https://maven.kotori316.com")
        content {
            includeModule("org.typelevel", "cats-core_3")
            includeModule("org.typelevel", "cats-kernel_3")
        }
    }
}

val commonProject = project.project(":test-utility:common")
dependencies {
    // implementation(group = "net.neoforged", name = "neoforge", version = project.property("neo_version").toString())

    compileOnly(commonProject)
}

tasks.processResources {
    from(commonProject.sourceSets.main.get().resources)
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
}

tasks.named("compileJava", JavaCompile::class).configure {
    source(commonProject.sourceSets.main.get().allSource)
}

tasks.test {
    useJUnitPlatform()
}

val jarAttributeMap = mapOf(
    "Specification-Title" to mod_id,
    "Specification-Vendor" to "Kotori316",
    "Specification-Version" to "1", // We are version 1 of ourselves
    "Implementation-Title" to project.name,
    "Implementation-Version" to tasks.jar.get().archiveVersion,
    "Implementation-Vendor" to "Kotori316",
    "Implementation-Timestamp" to ZonedDateTime.now().format(DateTimeFormatter.ISO_INSTANT),
    "MixinConfigs" to "${mod_id}.mixins.json",
    "Automatic-Module-Name" to mod_id,
)

tasks.jar {
    manifest {
        attributes(jarAttributeMap)
    }
}

tasks.register("deobfJar", Jar::class) {
    from(sourceSets.main.get().output)
    archiveClassifier.set("deobf")
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
    manifest {
        attributes(jarAttributeMap)
    }
}
