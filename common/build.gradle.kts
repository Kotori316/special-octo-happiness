plugins {
    java
    id("com.kotori316.common.java")
    id("com.kotori316.common.publish")
    id("org.spongepowered.gradle.vanilla") version ("0.2.1-SNAPSHOT")
}

val mc: String = project.property("minecraft").toString()

minecraft {
    version(mc)
}

repositories {
    maven { url = uri("https://maven.fabricmc.net/") }
}

dependencies {
    compileOnly("net.fabricmc:sponge-mixin:0.15.5+mixin.0.8.7")
}
