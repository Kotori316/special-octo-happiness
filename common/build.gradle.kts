plugins {
    java
    id("com.kotori316.common.java")
    alias(libs.plugins.spongepowered.vanilla)
}

val mc: String = project.property("minecraft").toString()

minecraft {
    version(mc)
}
