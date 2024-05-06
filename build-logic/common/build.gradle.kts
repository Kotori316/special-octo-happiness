plugins {
    `kotlin-dsl`
}

repositories {
    mavenCentral()
    gradlePluginPortal()
    maven { url = uri("https://maven.kotori316.com/") }
}

dependencies {
    /*mapOf(
        "com.kotori316.plugin.cf" to libs.versions.gradle.cf.get(),
    ).forEach { (name, version) ->
        implementation(group = name, name = "${name}.gradle.plugin", version = version)
    }*/
    implementation(group = "com.kotori316", name = "call-plugin", version = libs.versions.gradle.cf.get())
}
