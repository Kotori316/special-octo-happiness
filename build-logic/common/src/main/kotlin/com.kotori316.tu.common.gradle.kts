plugins {
    id("java")
    id("java-library")
}

base {
    group = "com.kotori316"
    archivesName = "${project.property("tu_archives_base_name")}-${project.name}"
    version = project.property("version")!!
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(17)
    }
    withSourcesJar()
}

tasks.withType(JavaCompile::class) {
    options.encoding = "UTF-8" // Use the UTF-8 charset for Java compilation
}

repositories {
    maven { url = uri("https://maven.parchmentmc.org") }
}

dependencies {
    api(platform("org.junit:junit-bom:${project.property("jupiter")}"))
    api("org.junit.jupiter:junit-jupiter")
    api("org.mockito:mockito-core:${project.property("mockito_core")}")
    api("org.mockito:mockito-inline:${project.property("mockito_inline")}")
}
