// Top-level build.gradle.kts

plugins {
    kotlin("jvm") version "1.9.0" apply false
    kotlin("android") version "1.9.0" apply false
    id("com.android.application") version "8.9.1" apply false
    id("com.android.library") version "8.9.1" apply false
}

tasks.register("clean", Delete::class) {
    delete(rootProject.buildDir)
}
