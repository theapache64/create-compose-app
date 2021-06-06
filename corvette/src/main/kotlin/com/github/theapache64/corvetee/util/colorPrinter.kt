package com.github.theapache64.corvetee.util

enum class Color(
    val code: String
) {
    RESET("\u001B[0m"),
    BLACK("\u001B[30m"),
    RED("\u001B[31m"),
    GREEN("\u001B[32m"),
    YELLOW("\u001B[33m"),
    BLUE("\u001B[34m"),
    PURPLE("\u001B[35m"),
    CYAN("\u001B[36m"),
    WHITE("\u001B[37m"),
}


fun println(color: Color, text: String) {
    println("${color.code}$text${Color.RESET.code}")
}

fun print(color: Color, text: String) {
    print("${color.code}$text${Color.RESET.code}")
}