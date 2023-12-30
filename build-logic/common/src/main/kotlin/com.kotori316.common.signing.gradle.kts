import org.gradle.configurationcache.extensions.capitalized

plugins {
    id("maven-publish")
    id("signing")
}

signing {
    sign(publishing.publications)
    listOf("jar", "deobfJar", "remapJar", "sourcesJar").forEach { name ->
        tasks.findByName(name)?.let {
            sign(it)
        }
    }
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

abstract class JarSignTask : DefaultTask() {
    @get:Input
    abstract val jarTask: Property<org.gradle.jvm.tasks.Jar>

    init {
        val canJarSign: Boolean = project.hasProperty("jarSign.keyAlias") &&
                project.hasProperty("jarSign.keyLocation") &&
                project.hasProperty("jarSign.storePass")
        onlyIf("runs only with jar sign keys") { canJarSign }
    }

    @TaskAction
    fun sign() {
        ant.withGroovyBuilder {
            "signjar"(
                "jar" to jarTask.get().archiveFile.get(),
                "alias" to project.findProperty("jarSign.keyAlias"),
                "keystore" to project.findProperty("jarSign.keyLocation"),
                "storepass" to project.findProperty("jarSign.storePass"),
                "sigalg" to "Ed25519",
                "digestalg" to "SHA-256",
                "tsaurl" to "http://timestamp.digicert.com",
            )
        }
    }
}

tasks {
    listOf("jar", "deobfJar", "remapJar", "sourcesJar").forEach { name ->
        findByName(name)?.let {
            val signTask = register("jksSign" + name.capitalized(), JarSignTask::class) {
                jarTask = it as org.gradle.jvm.tasks.Jar
            }
            it.finalizedBy(signTask)
        }
    }
}
