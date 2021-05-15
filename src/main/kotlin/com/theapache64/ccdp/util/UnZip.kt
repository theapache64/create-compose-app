package com.theapache64.ccdp.util


import java.nio.file.Path
import java.util.zip.ZipFile
import kotlin.io.path.*

fun Path.unzip(
    outputDir: Path = getDefaultOutputDir(this),
): Path {

    ZipFile(this.toFile()).use { zip ->
        zip.entries().asSequence().forEach { entry ->
            if (!entry.isDirectory) {
                zip.getInputStream(entry).use { input ->
                    val outputFile = outputDir / entry.name

                    with(outputFile) {
                        if (!outputFile.parent.exists()) {
                            parent.createDirectories()
                        }
                    }

                    outputFile.deleteIfExists()
                    outputFile.createFile()
                    outputFile.outputStream().use { output ->
                        input.copyTo(output)
                    }
                }
            }
        }
    }

    return outputDir
}

private fun getDefaultOutputDir(inputZipPath: Path): Path {
    return inputZipPath.parent / inputZipPath.nameWithoutExtension
}
