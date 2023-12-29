import com.kotori316.plugin.cf.CallVersionCheckFunctionTask
import com.kotori316.plugin.cf.CallVersionFunctionTask

plugins {
    id("java")
    id("maven-publish")
    id("com.kotori316.plugin.cf")
}

val mc: String = project.property("minecraft").toString()
val releaseDebug: Boolean = (System.getenv("RELEASE_DEBUG") ?: "true").toBoolean()

// configure the maven publication
publishing {
    publications {
        if (!releaseDebug) {
            create<MavenPublication>("mavenJava") {
                from(components["java"])
                artifactId = "debug-utility-${project.name}"
                pom {
                    description = "Debug Utility for Minecraft $mc for ${project.name}"
                }
            }
        }
        create<MavenPublication>("mavenLatest") {
            from(components["java"])
            artifactId = "debug-utility-${project.name}"
            version = project.property("maven_latest").toString()
            pom {
                description = "Debug Utility for Minecraft $mc for ${project.name}"
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
    platform = project.name
    modName = "debug-utility"
    changelog = "${project.version} for $mc in ${project.name}"
    homepage = "https://github.com/Kotori316/special-octo-happiness"
}

tasks.register("checkReleaseVersion", CallVersionCheckFunctionTask::class) {
    gameVersion = mc
    platform = project.name
    modName = "debug-utility"
    version = project.version.toString()
    failIfExists = !releaseDebug
}
