plugins {
    java
    id("com.kotori316.common.java")
    id("com.kotori316.common.publish")
    alias(libs.plugins.spongepowered.vanilla)
}

val mc: String = project.property("minecraft").toString()

minecraft {
    version(mc)
}

repositories {
    maven { url = uri("https://maven.fabricmc.net/") }
}

dependencies {
    compileOnly("net.fabricmc:sponge-mixin:0.17.4+mixin.0.8.7")
}
