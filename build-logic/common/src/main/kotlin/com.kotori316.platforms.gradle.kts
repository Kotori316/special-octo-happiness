plugins {
    id("java")
    id("maven-publish")
}

val mc: String = project.property("minecraft").toString()

// configure the maven publication
publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            from(components["java"])
            artifactId = "test-utility-${project.name}"
            pom {
                description = "Test Utility for Minecraft $mc for ${project.name}"
            }
        }
        create<MavenPublication>("mavenLatest") {
            from(components["java"])
            artifactId = "test-utility-${project.name}"
            version = project.property("maven_latest").toString()
            pom {
                description = "Test Utility for Minecraft $mc for ${project.name}"
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
