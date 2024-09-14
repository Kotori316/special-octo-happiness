plugins {
    java
    `java-library`
    idea
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
    withSourcesJar()
}

base {
    group = "com.kotori316"
    archivesName = "${project.property("archives_base_name")}-${project.name}"
    version = project.property("version")!!
}

tasks.withType(JavaCompile::class) {
    options.encoding = "UTF-8" // Use the UTF-8 charset for Java compilation
}

tasks.test {
    useJUnitPlatform()
    if (project.name.contains("neoforge")) {
        // Hack the NeoGradle setting, as it contains stupid configuration
        // disable test task as it fails due to accessing Minecraft resources
        // instead Neo adds another test task named "testJunit" and "build" depends on it
        enabled = false
    }
}

repositories {
    maven {
        name = "Minecraft-Manually"
        url = uri("https://libraries.minecraft.net/")
        content {
            includeGroup("org.lwjgl")
            includeGroup("com.mojang")
        }
    }
    maven { url = uri("https://maven.parchmentmc.org") }
    mavenCentral()
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:${project.property("jupiter")}"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")

    implementation("net.sf.jopt-simple:jopt-simple:5.0.4") { version { strictly("5.0.4") } }

    api(platform("org.junit:junit-bom:${project.property("jupiter")}"))
    api("org.junit.jupiter:junit-jupiter")
    api("org.mockito:mockito-core:${project.property("mockito_core")}")
    api("org.mockito:mockito-inline:${project.property("mockito_inline")}")
}

val mc: String = project.property("minecraft").toString()
val generalDescription = "special-octo-happiness(${project.version}) for Minecraft $mc with ${project.name}"
ext.set("generalDescription", generalDescription)

tasks {
    create("idePostSync")
}