plugins {
    id("java")
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

dependencies {
    implementation(platform("org.junit:junit-bom:${project.property("jupiter")}"))
    implementation("org.junit.jupiter:junit-jupiter")
    implementation("org.mockito:mockito-core:${project.property("mockito_core")}")
    implementation("org.mockito:mockito-inline:${project.property("mockito_inline")}")
}
