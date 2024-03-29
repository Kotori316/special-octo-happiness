@file:Suppress("UnstableApiUsage")

import org.gradle.jvm.tasks.Jar

plugins {
    id("fabric-loom").version("1.6.3")
    id("com.kotori316.du.common")
    id("com.kotori316.du.platforms")
}

repositories {
    // Add repositories to retrieve artifacts from in here.
    // You should only use this when depending on other mods because
    // Loom adds the essential maven repositories to download Minecraft and libraries from automatically.
    // See https://docs.gradle.org/current/userguide/declaring_repositories.html
    // for more information about repositories.
}

loom {
    mods {
        create(project.base.archivesName.get()) {
            sourceSet(sourceSets["main"])
        }
    }
}

val commonProject = project.findProject(":debug-utility:common")
dependencies {
    // To change the versions see the gradle.properties file
    minecraft("com.mojang:minecraft:${project.property("minecraft")}")
    mappings(loom.layered {
        officialMojangMappings()
        parchment("org.parchmentmc.data:parchment-${project.property("parchment_minecraft")}:${project.property("parchment_mapping")}@zip")
    })
    modImplementation("net.fabricmc:fabric-loader:${project.property("fabric_loader")}")
    modImplementation("net.fabricmc.fabric-api:fabric-api:${project.property("fabric_version")}")

    commonProject?.let { compileOnly(it) }
}

tasks.withType<ProcessResources> {
    commonProject?.let { from(it.sourceSets.main.get().resources) }
    inputs.property("version", project.version)
    filesMatching("fabric.mod.json") {
        expand(mapOf("version" to project.version))
    }
}

tasks.withType<Jar> {
    from("LICENSE") {
        rename { "${it}_${project.base.archivesName}" }
    }
}

tasks.withType<JavaCompile> {
    commonProject?.let { source(it.sourceSets.main.get().allSource) }
}

tasks.named("compileJava", JavaCompile::class) {
    dependsOn("processResources")
}
