plugins {
    id("java")
    id("com.kotori316.common.signing")
    id("com.kotori316.common.publish")
}

val mc: String = project.property("minecraft").toString()
val releaseDebug: Boolean = (System.getenv("RELEASE_DEBUG") ?: "true").toBoolean()
val pfName = project.name.split("-")[0]

val generalDescription = "Debug Utility(${project.version}) for Minecraft $mc with $pfName"
ext.set("generalDescription", generalDescription)
