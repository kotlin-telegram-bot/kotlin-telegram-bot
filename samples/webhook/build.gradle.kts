plugins {
    kotlin("jvm")
}
repositories {
    google()
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-stdlib")
    implementation("io.ktor:ktor-server-cio:1.6.4")
    implementation(project(":telegram"))
}

tasks.create<JavaExec>("runApp") {
    classpath = sourceSets.getByName("main").runtimeClasspath
    mainClass.set("com.github.kotlintelegrambot.webhook.MainKt")
}

tasks.jar {
    manifest {
        attributes(mapOf("Main-Class" to "com.github.kotlintelegrambot.MainKt"))
    }


    from({ configurations.getByName("compile").map { if (it.isDirectory) it else zipTree(it) } })
}
