plugins {
    id("java")
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
    withSourcesJar()
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
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:${project.property("jupiter")}"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")

    implementation("net.sf.jopt-simple:jopt-simple:5.0.4") { version { strictly("5.0.4") } }
}
