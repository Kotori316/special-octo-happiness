plugins {
    id("java")
    id("java-library")
    id("com.kotori316.common.java")
}

val pfName = project.name.split("-")[0]

base {
    group = "com.kotori316"
    archivesName = "${project.property("tu_archives_base_name")}-${pfName}"
    version = project.property("version")!!
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
