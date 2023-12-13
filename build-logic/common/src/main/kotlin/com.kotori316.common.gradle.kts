plugins {
    id("java")
    id("maven-publish")
    id("signing")
}

base {
    group = "com.kotori316"
    archivesName = "${project.property("archives_base_name")}-${project.name}"
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
