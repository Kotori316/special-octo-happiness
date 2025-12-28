plugins {
    java
    id("com.kotori316.common.java")
    id("org.spongepowered.gradle.vanilla") version ("0.3.0")
}

val mc: String = project.property("minecraft").toString()

minecraft {
    version(mc)
}
