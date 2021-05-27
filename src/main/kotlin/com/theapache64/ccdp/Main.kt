package com.theapache64.ccdp

import com.theapache64.ccdp.util.InputUtils
import com.theapache64.ccdp.util.unzip
import java.io.File
import java.io.FileOutputStream
import java.net.URL
import kotlin.io.path.*

private const val IS_DEBUG = true
private const val TEMPLATE_URL = "https://github.com/theapache64/compose-desktop-template/archive/refs/heads/master.zip"
private const val EXTRACTED_DIR_NAME = "compose-desktop-template-master"
private val REPLACEABLE_FILE_EXT = arrayOf("kt", "kts")
private const val MAIN_MODULE = "src"
private val MODULES = arrayOf(
    MAIN_MODULE, // main module
    "data", // data module
)
private val SRC_DIRS = arrayOf("main", "test")

fun main(args: Array<String>) {
    println("💻 Initializing...")
    // Ask project name
    val projectName = if (IS_DEBUG) {
        "Super Project"
    } else {
        InputUtils.promptString("Enter project name", true)
    }

    // Ask package name
    val packageName = if (IS_DEBUG) {
        "com.theapache64.superproject"
    } else {
        InputUtils.promptString("Enter package name", true)
    }

    val currentDir = if (IS_DEBUG) {
        "tmp"
    } else {
        System.getProperty("user.dir")
    }

    // Get source code
    println("⬇️  Downloading template...")
    val outputFile = Path(currentDir) / "compose-desktop-template.zip"
    if (outputFile.notExists()) {
        if (outputFile.parent.notExists()) {
            outputFile.parent.createDirectories()
        }
        val os = FileOutputStream(outputFile.toFile())
        URL(TEMPLATE_URL).openStream().copyTo(os)
    }

    // Unzip
    val extractDir = outputFile.parent
    println("📦 Unzipping...")
    outputFile.unzip(extractDir)

    // Rename dir
    val extractedProjectDir = extractDir / EXTRACTED_DIR_NAME
    val targetProjectDir = extractDir / projectName
    targetProjectDir.toFile().deleteRecursively()
    extractedProjectDir.moveTo(targetProjectDir, overwrite = true)

    // Move source
    println("🚚 Preparing source and test files (1/2) ...")
    for (module in MODULES) {
        for (type in SRC_DIRS) {
            val baseSrc = if (module == MAIN_MODULE) {
                // main module
                Path(module) / type / "kotlin"
            } else {
                Path(module) / "src" / type / "kotlin"
            }
            val myAppSrcPath = targetProjectDir / baseSrc / "com" / "myapp"
            if (myAppSrcPath.exists()) {
                val targetSrcPath = targetProjectDir / baseSrc / packageName.replace(".", File.separator)
                targetSrcPath.createDirectories()
                myAppSrcPath.moveTo(targetSrcPath, overwrite = true)
            }
        }
    }

    println("🚚 Verifying file contents (2/2) ...")
    val replaceMap = mapOf(
        "rootProject.name = \"compose-desktop-template\"" to "rootProject.name = \"$projectName\"", // settings.gradle.kt
        "mainClass = \"com.myapp.AppKt\"" to "mainClass = \"$packageName.AppKt\"", // build.gradle
        "packageName = \"myapp\"" to "packageName = \"$projectName\"", // build.gradle
        "com.myapp" to packageName, // app kt files
        "appName = \"My App\"," to "appName = \"$projectName\",", // App.kt
        "Hello Desktop!" to "Hello $projectName"
    )

    targetProjectDir.toFile().walk().forEach { file ->
        if (REPLACEABLE_FILE_EXT.contains(file.extension)) {
            var newContent = file.readText()
            for ((key, value) in replaceMap) {
                newContent = newContent.replace(
                    key, value
                )
            }
            file.writeText(newContent)
        }
    }

    // Give execute permission to ./gradlew
    val gradlewFile = targetProjectDir / "gradlew"
    gradlewFile.toFile().setExecutable(true, false)

    // Acknowledge
    if (!IS_DEBUG) {
        println("♻️  Removing temp files...")
        outputFile.deleteIfExists()
    }

    println("✔️  Finished. [Project Dir: '$targetProjectDir']")
}