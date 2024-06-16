plugins {
    id("java")
    id("java-library")
    id("com.kotori316.common.java")
}

val pfName = project.name.split("-")[0]

base {
    group = "com.kotori316"
    archivesName = "${project.property("du_archives_base_name")}-${pfName}"
    version = project.property("version")!!
}

repositories {
    maven { url = uri("https://maven.parchmentmc.org") }
}
