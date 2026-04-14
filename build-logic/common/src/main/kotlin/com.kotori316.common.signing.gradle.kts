plugins {
    java
    id("maven-publish")
    id("signing")
}

java {
    withSourcesJar()
}

signing {
    sign(publishing.publications)
}

// sign task creation is in `com.kotori316.jars.gradle.kts`
val hasGpgSignature = project.hasProperty("signing.keyId") &&
        project.hasProperty("signing.password") &&
        project.hasProperty("signing.secretKeyRingFile")

tasks.withType(Sign::class) {
    onlyIf("runs only with signing keys") { hasGpgSignature }
}

afterEvaluate {
    tasks.withType(AbstractPublishToMaven::class) {
        if (hasGpgSignature) {
            dependsOn(*tasks.filterIsInstance<Sign>().toTypedArray())
        }
    }
}

tasks {
    val signTask = register("jksSignJar", JarSignTask::class) {
        onlyIf {
            project.hasProperty("jarSign.keyAlias") &&
                    project.hasProperty("jarSign.keyLocation") &&
                    project.hasProperty("jarSign.storePass")
        }
        jarFile = jar.flatMap { it.archiveFile }
        keyAlias = project.findProperty("jarSign.keyAlias") as? String ?: ""
        keyStore = project.findProperty("jarSign.keyLocation") as? String ?: ""
        storePass = project.findProperty("jarSign.storePass") as? String ?: ""
    }
    jar {
        finalizedBy(signTask)
    }
}
