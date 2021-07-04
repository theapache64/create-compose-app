package com.github.theapache64.corvetee

import com.github.theapache64.corvetee.util.InputUtils
import com.github.theapache64.corvetee.util.unzip
import java.io.File
import java.io.FileOutputStream
import java.net.URL
import java.nio.file.Path
import kotlin.io.path.*

class Corvette(
    githubRepoUrl: String, // Eg :https://github.com/theapache64/compose-desktop-template
    private val srcPackagePath: Path = Path("com") / "myapp",
    private val modules: Array<String> = arrayOf(MAIN_MODULE),
    private val srcDirs: Array<String> = arrayOf("main", "test", "androidTest"),
    branch: String = "master",
    private val isDebug: Boolean = false,
    private val debugProjectName: String = "Super Project",
    private val debugPackageName: String = "com.theapache64.superproject",
) {

    companion object {
        const val MAIN_MODULE = "src"
        private val REPLACEABLE_FILE_EXT = arrayOf("kt", "kts", "html", "json", "xml", "gradle")
    }

    private val templateUrl = "$githubRepoUrl/archive/refs/heads/$branch.zip"
    private val repoName: String
    private val extractedDirName: String
    val projectName: String
    val packageName: String


    init {
        val urlSplit = githubRepoUrl.split("/")
        repoName = urlSplit.last()
        extractedDirName = "$repoName-master"

        // Ask project name
        projectName = if (isDebug) {
            debugProjectName
        } else {
            InputUtils.promptString("Enter project name", true)
        }

        // Ask package name
        packageName = if (isDebug) {
            debugPackageName
        } else {
            InputUtils.promptString("Enter package name", true)
        }
    }


    fun start(
        replaceMap: Map<String, String>,
        isAndroid: Boolean = false
    ) {
        println("💻 Initializing...")

        val currentDir = if (isDebug) {
            "tmp"
        } else {
            System.getProperty("user.dir")
        }

        // Get source code
        println("⬇️  Downloading template...")
        val outputFile = Path(currentDir) / "${repoName}.zip"
        if (outputFile.notExists()) {
            if (outputFile.parent.notExists()) {
                outputFile.parent.createDirectories()
            }
            val os = FileOutputStream(outputFile.toFile())
            URL(templateUrl).openStream().copyTo(os)
        }

        // Unzip
        val extractDir = outputFile.parent
        println("📦 Unzipping...")
        outputFile.unzip(extractDir)

        // Rename dir
        val extractedProjectDir = extractDir / extractedDirName
        val targetProjectDir = extractDir / projectName
        targetProjectDir.toFile().deleteRecursively()
        extractedProjectDir.moveTo(targetProjectDir, overwrite = true)

        // Move source
        println("🚚 Preparing source and test files (1/2) ...")
        for (module in modules) {
            for (type in srcDirs) {
                val baseSrc = when {
                    isAndroid -> {
                        Path(module) / type / "java"
                    }
                    module == MAIN_MODULE -> {
                        // main module
                        Path(module) / type / "kotlin"
                    }
                    else -> {
                        Path(module) / "src" / type / "kotlin"
                    }
                }
                val myAppSrcPath = if (isAndroid) {
                    targetProjectDir / "app" / baseSrc / srcPackagePath
                } else {
                    targetProjectDir / baseSrc / srcPackagePath
                }
                if (myAppSrcPath.exists()) {
                    val targetSrcPath = if (isAndroid) {
                        targetProjectDir / "app" / baseSrc / packageName.replace(".", File.separator)
                    } else {
                        targetProjectDir / baseSrc / packageName.replace(".", File.separator)
                    }
                    targetSrcPath.createDirectories()
                    myAppSrcPath.moveTo(targetSrcPath, overwrite = true)
                }
            }
        }

        println("🚚 Verifying file contents (2/2) ...")

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
        if (!isDebug) {
            println("♻️  Removing temp files...")
            outputFile.deleteIfExists()
        }

        println("✔️  Finished. [Project Dir: '$targetProjectDir']")
    }

}