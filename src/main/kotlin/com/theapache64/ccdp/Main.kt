package com.theapache64.ccdp

import com.theapache64.ccdp.util.unzip
import com.yg.kotlin.inquirer.components.promptInput
import com.yg.kotlin.inquirer.core.KInquirer
import java.io.FileOutputStream
import java.net.URL
import kotlin.io.path.Path
import kotlin.io.path.div

private const val TEMPLATE_URL = "https://github.com/theapache64/compose-desktop-template/archive/refs/heads/master.zip"
private const val IS_DEBUG = true
fun main(args: Array<String>) {
    // Ask project name
    val projectName = if (IS_DEBUG) {
        "Super Project"
    } else {
        KInquirer.promptInput("Enter project name:")
    }

    // Ask package name
    val packageName = if (IS_DEBUG) {
        "com.theapache64.superproject"
    } else {
        KInquirer.promptInput("Enter package name:")
    }

    val currentDir = if (IS_DEBUG) {
        "build"
    } else {
        System.getProperty("user.dir")
    }

    // Get source code
    println("Downloading template...")
    val outputFile = Path(currentDir) / "compose-desktop-template.zip"
    val os = FileOutputStream(outputFile.toFile())
    URL(TEMPLATE_URL).openStream().copyTo(os)

    // Unzip
    println("Unzipping...")
    outputFile.unzip(outputFile.parent)

    // Replace project name

    // Replace package name

    // Acknowledge
}