import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    id("java")
    id("com.github.johnrengelman.shadow").version("6.0.0")
}

group = "org.example"
version = "1.0.0-SNAPSHOT"

repositories {
    mavenCentral()
    mavenLocal()
    maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
    maven("https://oss.sonatype.org/content/repositories/snapshots")
    maven("https://oss.sonatype.org/content/repositories/central")
    maven("https://repo.codemc.io/repository/maven-public/")
}

val projectFullName = "${project.name}-LATEST.jar"

dependencies {
    compileOnly("org.jetbrains:annotations:24.0.0")
    compileOnly("org.spigotmc:spigot-api:1.8.8-R0.1-SNAPSHOT")
    compileOnly("com.worldplugins:WorldLib:LATEST")
    compileOnly("me.filoghost.holographicdisplays:holographicdisplays-api:3.0.0")
}

tasks.getByName<Test>("test") {
    useJUnitPlatform()
}

tasks.withType<ShadowJar> {
    archiveFileName.set(projectFullName);
}

task("shadowAndCopy") {
    group = "Build"
    description = "Copies the jar into the location of the OUTPUT_PATH variable"
    dependsOn("shadowJar")

    val copyTask: (String, Int) -> Copy = { dest, id ->
        tasks.create("copyTaskExec_$id", Copy::class) {
            from(layout.buildDirectory.dir("libs"))
            into(dest)
        }
    }

    val deleteTask: (String, Int) -> Delete = { dir, id ->
        tasks.create("deleteTaskExec_$id", Delete::class) {
            val filePath = "$dir/$projectFullName"
            delete(filePath)
        }
    }

    fun build(dirsRaw: String) {
        System.getenv(dirsRaw).split(",").forEachIndexed { idx, dest ->
            deleteTask(dest, idx).run { actions[0].execute(this) }
            copyTask(dest, idx).run { actions[0].execute(this) }
        }
    }

    doLast {
        build("paths")
    }
}
