plugins {
    java
    id("org.spongepowered.gradle.vanilla") version ("0.2.1-SNAPSHOT")
    id("com.kotori316.du.common")
}

val mc: String = project.property("minecraft").toString()

minecraft {
    version(mc)
}
