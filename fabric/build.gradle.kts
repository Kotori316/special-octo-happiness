import org.gradle.jvm.tasks.Jar

plugins {
    id("com.kotori316.common.java")
    id("com.kotori316.common.publish")
    // id("com.kotori316.common.signing")
    alias(libs.plugins.fabric.loom)
}

loom {
    mods {
        create(project.base.archivesName.get()) {
            sourceSet(sourceSets["main"])
        }
    }
    runs {
        create("gameTestServer") {
            configName = "GameTestServer"
            server()
            vmArgs(
                "-ea",
                "-Dfabric-api.gametest",
                "-Dfabric-api.gametest.report-file=game_test/test-results/test/game_test.xml"
            )
            runDir = "run-server"
        }
    }
}

val commonProject = project.findProject(":common")
dependencies {
    // To change the versions see the gradle.properties file
    minecraft("com.mojang:minecraft:${project.property("minecraft")}")
    implementation("net.fabricmc:fabric-loader:${project.property("fabric_loader")}")
    implementation("net.fabricmc.fabric-api:fabric-api:${project.property("fabric_version")}")

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

afterEvaluate {
    println(tasks.jar.get().archiveFile.get())
    println(publishing.publications)
    println(components["java"])
}
