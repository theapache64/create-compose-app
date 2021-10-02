package com.theapache64.createcomposeapp

import com.github.theapache64.corvetee.Corvette
import com.github.theapache64.corvetee.util.Color
import com.github.theapache64.corvetee.util.InputUtils
import com.github.theapache64.corvetee.util.println
import kotlin.io.path.Path
import kotlin.io.path.div

private const val IS_DEBUG = false

enum class Platform(val title: String) {
    Android("Android"),
    Desktop("Desktop"),
    Web("Web"),
    ChromeExt("Chrome extension"),
    DesktopGame("Desktop (game)"),
}


fun main(args: Array<String>) {

    val platform = if (IS_DEBUG) {
        Platform.DesktopGame
    } else {
        println(Color.YELLOW, "Choose platform")
        val platforms = Platform.values()

        for ((index, p) in platforms.withIndex()) {
            println("${index + 1}) ${p.title}")
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
        Platform.Desktop -> createDesktopApp()
        Platform.Android -> createAndroidApp()
        Platform.Web -> createComposeWebApp()
        Platform.ChromeExt -> createChromeExtensionApp()
        Platform.DesktopGame -> createDesktopGameApp()
    }
}

fun createAndroidApp() {
    val corvette = Corvette(
        githubRepoUrl = "https://github.com/theapache64/compose-android-template",
        isDebug = IS_DEBUG,
        srcPackagePath = Path("com") / "theapache64" / "composeandroidtemplate"
    )

    val replaceMap = mapOf(
        "rootProject.name = \"compose-android-template\"" to "rootProject.name = \"${corvette.projectName}\"", // settings.build.gradle
        "com.theapache64.composeandroidtemplate" to corvette.packageName,
        "<string name=\"app_name\">compose-android-template</string>" to "<string name=\"app_name\">${corvette.projectName}</string>",
        "ComposeAndroidTemplate" to corvette.projectName,
    )

    corvette.start(replaceMap, isAndroid = true)
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
    println(
        Color.YELLOW,
        "Run `./gradlew jsBrowserRun` from project root to run the extension directly in your browser"
    )
}

private fun createDesktopApp() {

    val corvette = Corvette(
        githubRepoUrl = "https://github.com/theapache64/compose-desktop-template",
        modules = arrayOf(
            "src",
            "data"
        ),
        isDebug = IS_DEBUG
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

private fun createDesktopGameApp() {
    val corvette = Corvette(
        githubRepoUrl = "https://github.com/theapache64/compose-desktop-game-template",
        isDebug = IS_DEBUG,
        modules = arrayOf("src"),
        srcDirs = arrayOf("main"),
        srcPackagePath = Path("com") / "mygame"
    )

    val replaceMap = mapOf(
        "rootProject.name = \"compose-desktop-game-template\"" to "rootProject.name = \"${corvette.projectName}\"", // settings.gradle.kt
        "mainClass = \"com.mygame.MainKt\"" to "mainClass = \"${corvette.packageName}.MainKt\"", // build.gradle
        "packageName = \"compose-desktop-game-template\"" to "packageName = \"${corvette.projectName}\"", // build.gradle
        "com.mygame" to corvette.packageName, // app kt files
    )

    corvette.start(replaceMap)
    println(Color.YELLOW, "Run `./gradlew run` from project root to run the game")
}