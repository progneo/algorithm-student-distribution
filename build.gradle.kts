import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.7.21"
    id("maven-publish")
    application
}

group = "ru.student.distribution"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))

    implementation("org.apache.poi:poi-ooxml:5.2.2")
    implementation("org.apache.commons:commons-csv:1.9.0")
    implementation("com.google.code.gson:gson:2.10")
    implementation("com.grapecity.documents:gcexcel:5.0.3")
    implementation("com.github.doyaaaaaken:kotlin-csv-jvm:1.6.0")
}

tasks.test {
    useJUnitPlatform()
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}

application {
    mainClass.set("MainKt")
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            groupId = "ru.student.distribution"
            artifactId = "student-distribution-algorithm"
            version = "1.1.8"

            from(components["java"])
        }
    }
}
