import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
	kotlin("jvm") version "1.5.30"
}

group = "me.kononov"
version = "0.0.1-SNAPSHOT"

repositories {
	mavenCentral()
}

dependencies {
    implementation(kotlin("stdlib"))
}

kotlin {
    sourceSets["main"].apply {
        kotlin.srcDir("main/kotlin")
    }
}

tasks.withType<Test> {
	useJUnitPlatform()
}

tasks.withType<KotlinCompile> {
	kotlinOptions {
		jvmTarget = "16"
	}
}
