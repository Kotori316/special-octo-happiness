plugins {
    id("java")
    id("com.kotori316.common.signing")
    id("com.kotori316.common.publish")
}

val mc: String = project.property("minecraft").toString()
val releaseDebug: Boolean = (System.getenv("RELEASE_DEBUG") ?: "true").toBoolean()

val generalDescription = "Debug Utility(${project.version}) for Minecraft $mc with ${project.name}"
ext.set("generalDescription", generalDescription)
