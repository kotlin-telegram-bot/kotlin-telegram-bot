import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

buildscript {
    repositories {
        mavenCentral()
    }
    
    dependencies { 
        classpath("org.jetbrains.kotlinx:atomicfu-gradle-plugin:0.17.0")
    }
}

plugins {
    kotlin("jvm") version "1.6.10"
}

allprojects {
    apply(from = "$rootDir/ktlint.gradle")

    group = "com.github.kotlintelegrambot"
    version = "0.3.4"

    repositories {
        mavenCentral()
    }
    
    tasks.withType<KotlinCompile> {
        kotlinOptions {
            freeCompilerArgs += "-opt-in=kotlin.RequiresOptIn"
        }
    }
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-stdlib:1.6.10")
}
