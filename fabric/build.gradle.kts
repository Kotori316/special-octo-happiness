import org.gradle.jvm.tasks.Jar

plugins {
    id("com.kotori316.common.java")
    id("com.kotori316.common.publish")
    // id("com.kotori316.common.signing")
    id("fabric-loom").version("1.8.2")
}

loom {
    mods {
        create(project.base.archivesName.get()) {
            sourceSet(sourceSets["main"])
        }
    }
}

val commonProject = project.findProject(":common")
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
