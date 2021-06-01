package com.theapache64.ccdp

import com.github.theapache64.corvetee.Corvette

fun main(args: Array<String>) {

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