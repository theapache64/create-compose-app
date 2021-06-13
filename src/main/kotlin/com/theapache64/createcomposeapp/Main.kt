package com.theapache64.createcomposeapp

import com.github.theapache64.corvetee.Corvette
import com.github.theapache64.corvetee.util.Color
import com.github.theapache64.corvetee.util.InputUtils
import com.github.theapache64.corvetee.util.println

private const val PLATFORM_DESKTOP = "Desktop"
private const val PLATFORM_WEB = "Web"
private const val PLATFORM_CHROME_EXT = "Chrome"
private const val IS_DEBUG = false

private val platforms = listOf(
    PLATFORM_DESKTOP,
    PLATFORM_WEB,
    PLATFORM_CHROME_EXT,
)

fun main(args: Array<String>) {

    val platform = if (IS_DEBUG) {
        PLATFORM_DESKTOP
    } else {
        println(Color.YELLOW, "Choose platform")
        for ((index, p) in platforms.withIndex()) {
            println("${index + 1}) $p")
        }
        val selPlatformIndex = InputUtils.getInt(
            "Choose platform #",
            1,
            platforms.size
        )

        platforms[selPlatformIndex - 1]
    }

    println(Color.CYAN, "Platform: $platform")

    when (platform) {
        PLATFORM_DESKTOP -> createDesktopApp()
        PLATFORM_WEB -> createComposeWebApp()
        PLATFORM_CHROME_EXT -> createChromeExtensionApp()
    }
}

fun createComposeWebApp() {
    val corvette = Corvette(
        githubRepoUrl = "https://github.com/theapache64/compose-web-template",
        isDebug = IS_DEBUG
    )

    val replaceMap = mapOf(
        "rootProject.name = \"compose-web-template\"" to "rootProject.name = \"${
            corvette.projectName.replace(
                " ",
                "_"
            )
        }\"", // settings.gradle.kt
        "com.theapache64" to corvette.packageName, // app kt files
        "<script src=\"compose-web-template.js\"></script>" to "<script src=\"${
            corvette.projectName.replace(
                " ",
                "_"
            )
        }.js\"></script>", // index.html
        "Compose Web Template" to corvette.projectName // index.html
    )

    corvette.start(replaceMap)
    println(Color.YELLOW, "Run `./gradlew jsBrowserRun` from project root to run the app in your browser")
}

fun createChromeExtensionApp() {
    val corvette = Corvette(
        githubRepoUrl = "https://github.com/theapache64/compose-chrome-extension-template",
        isDebug = IS_DEBUG
    )

    val replaceMap = mapOf(
        "rootProject.name = \"compose-chrome-extension-template\"" to "rootProject.name = \"${
            corvette.projectName.replace(
                " ",
                "_"
            )
        }\"", // settings.gradle.kt
        "com.composeweb.chrome" to corvette.packageName, // app kt files
        "<script src=\"compose-chrome-extension-template.js\"></script>" to "<script src=\"${
            corvette.projectName.replace(
                " ",
                "_"
            )
        }.js\"></script>", // index.html
        "Compose Chrome Extension Template" to corvette.projectName, // index.html
        "platform = \"Chrome Extension!\"" to "platform = \"${corvette.projectName}!\"",
    )

    corvette.start(replaceMap)
    println(Color.YELLOW, "Run `./gradlew jsBrowserRun` from project root to run the extension directly in your browser")
}

private fun createDesktopApp() {

    val corvette = Corvette(
        githubRepoUrl = "https://github.com/theapache64/compose-desktop-template",
        modules = arrayOf(
            "src",
            "data"
        ),
        isDebug = false
    )

    val replaceMap = mapOf(
        "rootProject.name = \"compose-desktop-template\"" to "rootProject.name = \"${corvette.projectName}\"", // settings.gradle.kt
        "mainClass = \"com.myapp.AppKt\"" to "mainClass = \"${corvette.packageName}.AppKt\"", // build.gradle
        "packageName = \"myapp\"" to "packageName = \"${corvette.projectName}\"", // build.gradle
        "com.myapp" to corvette.packageName, // app kt files
        "appName = \"My App\"," to "appName = \"${corvette.projectName}\",", // App.kt
        "Hello Desktop!" to "Hello ${corvette.projectName}"
    )

    corvette.start(replaceMap)
    println(Color.YELLOW, "Run `./gradlew run` from project root to run the app")
}