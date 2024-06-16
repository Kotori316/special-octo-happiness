import com.kotori316.plugin.cf.CallVersionCheckFunctionTask
import com.kotori316.plugin.cf.CallVersionFunctionTask

plugins {
    id("maven-publish")
    id("com.kotori316.plugin.cf")
}

val mc: String = project.property("minecraft").toString()
val releaseDebug: Boolean = (System.getenv("RELEASE_DEBUG") ?: "true").toBoolean()

fun artifactName(): String {
    val p = project.path
    return p.split(':')[1]
}

fun pfVersion(platform: String): String {
    return when (platform) {
        "forge" -> project.property("forge_version").toString()
        "fabric" -> project.property("fabric_version").toString()
        "neoforge" -> project.property("neo_version").toString()
        else -> throw IllegalArgumentException("Unknown platform: $platform")
    }
}

val pfName = project.name.split("-")[0]

// configure the maven publication
publishing {
    publications {
        if (!releaseDebug) {
            create<MavenPublication>("mavenJava") {
                from(components["java"])
                artifactId = "${artifactName()}-${pfName}"
                pom {
                    description = project.provider { project.ext.get("generalDescription") as String }
                }
            }
        }
        create<MavenPublication>("mavenLatest") {
            from(components["java"])
            artifactId = "${artifactName()}-${pfName}"
            version = project.property("maven_latest").toString()
            pom {
                description = project.provider { project.ext.get("generalDescription") as String }
            }
        }
    }

    repositories {
        if (System.getenv("CLOUDFLARE_S3_ENDPOINT") != null) {
            val r2AccessKey = (project.findProperty("r2_access_key") ?: System.getenv("R2_ACCESS_KEY") ?: "") as String
            val r2SecretKey = (project.findProperty("r2_secret_key") ?: System.getenv("R2_SECRET_KEY") ?: "") as String
            maven {
                name = "kotori316-maven"
                url = uri("s3://kotori316-maven")
                credentials(AwsCredentials::class) {
                    accessKey = r2AccessKey
                    secretKey = r2SecretKey
                }
            }
        }
    }
}


tasks.register("registerVersion", CallVersionFunctionTask::class) {
    functionEndpoint = CallVersionFunctionTask.readVersionFunctionEndpoint(project)
    gameVersion = mc
    platform = pfName
    platformVersion = pfVersion(pfName)
    modName = artifactName()
    changelog = project.provider { project.ext.get("generalDescription") as String }
    homepage = "https://github.com/Kotori316/special-octo-happiness"
    isDryRun = releaseDebug
}

tasks.register("checkReleaseVersion", CallVersionCheckFunctionTask::class) {
    gameVersion = mc
    platform = pfName
    modName = artifactName()
    version = project.version.toString()
    failIfExists = !releaseDebug
}
