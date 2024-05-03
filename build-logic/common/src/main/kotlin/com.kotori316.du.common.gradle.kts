plugins {
    id("java")
    id("java-library")
    id("com.kotori316.common.java")
}

base {
    group = "com.kotori316"
    archivesName = "${project.property("du_archives_base_name")}-${project.name}"
    version = project.property("version")!!
}

repositories {
    maven { url = uri("https://maven.parchmentmc.org") }
}
