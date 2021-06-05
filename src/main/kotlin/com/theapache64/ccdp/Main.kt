package com.theapache64.ccdp

import com.github.theapache64.corvetee.Corvette
import com.yg.kotlin.inquirer.components.promptList
import com.yg.kotlin.inquirer.core.KInquirer

private const val PLATFORM_DESKTOP = "Desktop"
private const val PLATFORM_WEB = "Web"
private const val PLATFORM_ANDROID = "Android"
private const val IS_DEBUG = true

fun main(args: Array<String>) {

    val platform = if (IS_DEBUG) {
        PLATFORM_WEB
    } else {
        KInquirer.promptList(
            message = "Choose platform",
            listOf(
                PLATFORM_DESKTOP,
                PLATFORM_WEB,
                PLATFORM_ANDROID,
            )
        )
    }

    when (platform) {
        PLATFORM_DESKTOP -> {
            createDesktopApp()
        }
        PLATFORM_WEB -> {
            createComposeWebApp()
        }
        PLATFORM_ANDROID -> {
            println("// TODO : Coming soon")
        }
    }
}

fun createComposeWebApp() {
    val corvette = Corvette(
        githubRepoUrl = "https://github.com/theapache64/compose-web-template",
        isDebug = IS_DEBUG
    )

    val replaceMap = mapOf(
        "rootProject.name = \"compose-web-template\"" to "rootProject.name = \"${corvette.projectName.replace(" ","_")}\"", // settings.gradle.kt
        "com.theapache64" to corvette.packageName, // app kt files
        "<script src=\"compose-web-template.js\"></script>" to "<script src=\"${corvette.projectName.replace(" ","_")}.js\"></script>", // index.html
        "Compose Web Template" to corvette.projectName // index.html
    )

    corvette.start(replaceMap)
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
}